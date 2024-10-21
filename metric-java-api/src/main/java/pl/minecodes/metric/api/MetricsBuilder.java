package pl.minecodes.metric.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class MetricsBuilder {

  private final Logger logger = Logger.getLogger("Metrics");
  private final HttpClient httpClient = HttpClient.newBuilder().build();

  private final String endpoint;
  private final long projectId;
  private final String integrationKey;
  private final List<Metric> metrics = new ArrayList<>();

  public MetricsBuilder(String endpoint, long projectId, String integrationKey) {
    this.endpoint = endpoint;
    this.projectId = projectId;
    this.integrationKey = integrationKey;
  }

  public MetricsBuilder addMetrics(String name, String value, MetricValueType valueType) {
    this.metrics.add(new Metric(this.projectId, name, value, valueType));
    return this;
  }

  public void publish() {
    try {
      String body = convertMetricsToJson();

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(this.endpoint))
          .PUT(BodyPublishers.ofString(body))
          .header("Content-Type", "application/json")
          .header("X-INTEGRATION-KEY", this.integrationKey)
          .build();

      HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
      if (response.statusCode() != 201) {
        throw new RuntimeException("Bad status code.");
      }
    } catch (Exception exception) {
      this.logger.info("Metrics can't be published.");
    }
  }

  private String convertMetricsToJson() {
    JSONArray jsonArray = new JSONArray();
    for (Metric metric : this.metrics) {
      JSONObject jsonMetric = new JSONObject();
      jsonMetric.put("projectId", metric.getProjectId());
      jsonMetric.put("name", metric.getName());
      jsonMetric.put("value", metric.getValue());
      jsonMetric.put("valueType", metric.getValueType().toString());
      jsonArray.put(jsonMetric);
    }
    return jsonArray.toString();
  }
}
