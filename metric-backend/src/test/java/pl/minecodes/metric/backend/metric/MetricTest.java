package pl.minecodes.metric.backend.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import pl.minecodes.metric.backend.metric.exception.MetricValueTypeException;

class MetricTest {

  @Test
  void getTypedValueShouldReturnIntegerForIntegerType() {
    Metric metric = new Metric(1L, 1L, "views", "100", Instant.now(), "127.0.0.1", MetricValueType.INTEGER);
    Integer result = metric.getTypedValue();
    assertEquals(100, result);
  }

  @Test
  void getTypedValueShouldReturnDoubleForDoubleType() {
    Metric metric = new Metric(1L, 1L, "temperature", "25.5", Instant.now(), "127.0.0.1", MetricValueType.DOUBLE);
    Double result = metric.getTypedValue();
    assertEquals(25.5, result, 0.001);
  }

  @Test
  void getTypedValueShouldReturnStringForStringType() {
    Metric metric = new Metric(1L, 1L, "version", "v1.0.0", Instant.now(), "127.0.0.1", MetricValueType.STRING);
    String result = metric.getTypedValue();
    assertEquals("v1.0.0", result);
  }

  @Test
  void getTypedValueShouldReturnBooleanForBooleanType() {
    Metric metric = new Metric(1L, 1L, "isActive", "true", Instant.now(), "127.0.0.1", MetricValueType.BOOLEAN);
    Boolean result = metric.getTypedValue();
    assertTrue(result);
  }

  @Test
  void getTypedValueShouldThrowExceptionForInvalidInteger() {
    Metric metric = new Metric(1L, 1L, "views", "not a number", Instant.now(), "127.0.0.1", MetricValueType.INTEGER);
    assertThrows(MetricValueTypeException.class, metric::getTypedValue);
  }

  @Test
  void getTypedValueShouldThrowExceptionForInvalidDouble() {
    Metric metric = new Metric(1L, 1L, "temperature", "not a number", Instant.now(), "127.0.0.1", MetricValueType.DOUBLE);
    assertThrows(MetricValueTypeException.class, metric::getTypedValue);
  }

  @Test
  void getTypedValueShouldThrowExceptionForInvalidBoolean() {
    Metric metric = new Metric(1L, 1L, "isActive", "not a boolean", Instant.now(), "127.0.0.1", MetricValueType.BOOLEAN);
    assertThrows(MetricValueTypeException.class, metric::getTypedValue);
  }

  @Test
  void getTypedValueShouldReturnNullForNullValue() {
    Metric metric = new Metric(1L, 1L, "nullMetric", null, Instant.now(), "127.0.0.1", MetricValueType.STRING);
    assertNull(metric.getTypedValue());
  }

  @Test
  void metricShouldBeCorrectlyInstantiated() {
    Instant now = Instant.now();
    Metric metric = new Metric(1L, 2L, "testMetric", "testValue", now, "192.168.0.1", MetricValueType.STRING);

    assertEquals(1L, metric.getId());
    assertEquals(2L, metric.getProjectId());
    assertEquals("testMetric", metric.getName());
    assertEquals("testValue", metric.getValue());
    assertEquals(now, metric.getDate());
    assertEquals("192.168.0.1", metric.getAddress());
    assertEquals(MetricValueType.STRING, metric.getValueType());
  }
}