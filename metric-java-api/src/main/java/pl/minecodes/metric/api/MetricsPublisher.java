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

public class MetricsPublisher {

  private final Logger logger = Logger.getLogger("Metrics");
  private final HttpClient httpClient = HttpClient.newBuilder().build();

  private boolean debug;

  private final MetricAuth auth;
  private final List<Metric> metrics = new ArrayList<>();

  public MetricsPublisher(MetricAuth auth) {
    this.auth = auth;
  }

  public MetricsPublisher addMetrics(String name, String value) {
    this.metrics.add(new Metric(this.auth.getProjectId(), name, value, MetricValueType.STRING));
    return this;
  }

  public MetricsPublisher addMetrics(String name, Integer value) {
    this.metrics.add(new Metric(this.auth.getProjectId(), name, Integer.toString(value), MetricValueType.INTEGER));
    return this;
  }

  public MetricsPublisher addMetrics(String name, Double value) {
    this.metrics.add(new Metric(this.auth.getProjectId(), name, Double.toString(value), MetricValueType.DOUBLE));
    return this;
  }

  public MetricsPublisher addMetrics(String name, Boolean value) {
    this.metrics.add(new Metric(this.auth.getProjectId(), name, Boolean.toString(value), MetricValueType.BOOLEAN));
    return this;
  }

  public MetricsPublisher withDebug() {
    this.debug = true;
    return this;
  }

  public void publish() {
    try {
      String body = convertMetricsToJson();

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(this.auth.getEndpoint()))
          .PUT(BodyPublishers.ofString(body))
          .header("Content-Type", "application/json")
          .header("X-INTEGRATION-KEY", this.auth.getIntegrationKey())
          .build();

      HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
      if (response.statusCode() != 201) {
        if (debug) {
          this.logger.severe("Bad status code %s".formatted(response.statusCode()));
        }

        throw new RuntimeException("Bad status code.");
      }
    } catch (Exception exception) {
      if (debug) {
        exception.printStackTrace();
      }

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
