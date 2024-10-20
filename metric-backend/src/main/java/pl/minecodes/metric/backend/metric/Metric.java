package pl.minecodes.metric.backend.metric;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.minecodes.metric.backend.metric.exception.MetricValueTypeException;

@Data
@AllArgsConstructor
@Document(collection = "mm_metrics")
public class Metric {

  @Transient
  public static final String SEQUENCE_NAME = "metric_sequence";

  private final long id;
  private final long projectId;
  private final String name;
  private final String value;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Warsaw")
  private final Instant date;
  private final String address;
  private final MetricValueType valueType;

  @SuppressWarnings("unchecked")
  public <T> T getTypedValue() {
    if (this.value == null) {
      return null;
    }

    try {
      return (T) this.valueType.parseValue(this.value);
    } catch (IllegalArgumentException e) {
      throw new MetricValueTypeException("Invalid value for type " + valueType + ": " + value, e);
    }
  }
}
