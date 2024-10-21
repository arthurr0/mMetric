package pl.minecodes.metric.api;

public class MetricAuth {

  private final long projectId;
  private final String endpoint;
  private final String integrationKey;

  public MetricAuth(long projectId, String endpoint, String integrationKey) {
    this.projectId = projectId;
    this.endpoint = endpoint;
    this.integrationKey = integrationKey;
  }

  public long getProjectId() {
    return projectId;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getIntegrationKey() {
    return integrationKey;
  }
}
