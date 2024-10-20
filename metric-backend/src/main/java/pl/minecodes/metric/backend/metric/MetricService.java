package pl.minecodes.metric.backend.metric;

import java.time.Instant;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class MetricService {

  private final MetricRepository metricRepository;

  public MetricService(MetricRepository metricRepository) {
    this.metricRepository = metricRepository;
  }

  public List<Metric> findAll(String name, long projectId) {
    return this.metricRepository.findAll(name, projectId);
  }

  public List<Metric> findAllBetweenDates(String name, long projectId, Instant from, Instant to) {
    return this.metricRepository.findAllBetweenDates(name, projectId, from, to);
  }

  public MetricValueType getValueTypeForMetricName(String name, long projectId) {
    List<Metric> metrics = this.metricRepository.findAll(name, projectId);
    if (metrics.isEmpty()) {
      return null;
    }

    return metrics.getFirst().getValueType();
  }

  @Nullable
  public MetricValueType getMetricValueTypeForList(List<Metric> metrics) {
    if (metrics.isEmpty()) {
      return null;
    }

    return metrics.getFirst().getValueType();
  }

  public void save(Metric metric) {
    this.metricRepository.save(metric);
  }

}
