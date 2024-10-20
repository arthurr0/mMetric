package pl.minecodes.metric.backend.metric.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.minecodes.metric.backend.metric.MetricValueType;

@Data
@AllArgsConstructor
public class MetricReceiveRequest {

  private final long projectId;
  private final String name;
  private final String value;
  private final MetricValueType valueType;

}
