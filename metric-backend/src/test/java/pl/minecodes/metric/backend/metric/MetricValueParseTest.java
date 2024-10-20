package pl.minecodes.metric.backend.metric;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class MetricValueParseTest {

  @Test
  void testIntegerParse() {
    Metric test = new Metric(0, 0, "test", "10", Instant.now(), "0.0.0.0", MetricValueType.INTEGER);
    Integer typedValue = test.getTypedValue();

    assertThat(typedValue).isEqualTo(10);
  }

}
