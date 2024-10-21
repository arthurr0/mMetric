package pl.minecodes.metric.api;

public class Metric {

  private final long projectId;
  private final String name;
  private final String value;
  private final MetricValueType valueType;

  public Metric(long projectId, String name, String value, MetricValueType valueType) {
    this.projectId = projectId;
    this.name = name;
    this.value = value;
    this.valueType = valueType;
  }

  public long getProjectId() {
    return projectId;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public MetricValueType getValueType() {
    return valueType;
  }
}
