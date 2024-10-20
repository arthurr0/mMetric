package pl.minecodes.metric.backend.metric.request;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.minecodes.metric.backend.metric.MetricOperation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricGetRequest {

  private long projectId;
  private String name;
  private MetricOperation operation;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant dateFrom = null;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant dateTo = null;

  public boolean withDate() {
    return dateFrom != null || dateTo != null;
  }

}
