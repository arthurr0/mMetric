package pl.minecodes.metric.backend.metric.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.minecodes.metric.backend.metric.MetricOperation;

@Data
@AllArgsConstructor
public class MetricResponse<T> {

  private final T value;
  private final String name;
  private final long projectId;
  private final MetricOperation operation;

}
