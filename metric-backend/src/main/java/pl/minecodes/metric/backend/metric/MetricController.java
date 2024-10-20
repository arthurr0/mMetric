package pl.minecodes.metric.backend.metric;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.minecodes.metric.backend.metric.exception.MetricGetException;
import pl.minecodes.metric.backend.metric.exception.MetricReceiveException;
import pl.minecodes.metric.backend.metric.request.MetricGetRequest;
import pl.minecodes.metric.backend.metric.request.MetricReceiveRequest;
import pl.minecodes.metric.backend.metric.response.MetricResponse;
import pl.minecodes.metric.backend.project.Project;
import pl.minecodes.metric.backend.project.ProjectService;

@RestController
@RequestMapping("/api/v1/metric")
class MetricController {

  private final MetricFactory metricFactory;
  private final MetricService metricService;
  private final ProjectService projectService;

  public MetricController(
      MetricFactory metricFactory,
      MetricService metricService,
      ProjectService projectService
  ) {
    this.metricFactory = metricFactory;
    this.metricService = metricService;
    this.projectService = projectService;
  }

  @PutMapping
  public ResponseEntity<?> receiveMetrics(@RequestBody List<MetricReceiveRequest> requests, HttpServletRequest servletRequest) {
    if (requests.isEmpty()) {
      throw new MetricReceiveException("Metrics are empty.");
    }

    requests.forEach(request -> {
      if (!this.projectService.exists(request.getProjectId())) {
        throw new MetricReceiveException("Provided project id is not exists.");
      }

      Metric metric = this.metricFactory.createFromRequest(request, servletRequest);

      MetricValueType valueType = this.metricService.getValueTypeForMetricName(metric.getName(), metric.getProjectId());

      if (valueType != null && metric.getValueType() != valueType) {
        throw new MetricReceiveException("The specified value type for (%s) is different than before. Please use %s.".formatted(metric.getName(), valueType));
      }

      this.metricService.save(metric);
    });

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping
  public ResponseEntity<?> getMetric(@RequestBody MetricGetRequest request) {
    Optional<Project> projectOptional = this.projectService.findById(request.getProjectId());
    if (projectOptional.isEmpty()) {
      throw new MetricGetException("Project with provided id is not exists.");
    }

    Project project = projectOptional.get();

    List<Metric> metrics;
    if (request.withDate()) {
      metrics = this.metricService.findAllBetweenDates(request.getName(), project.getId(), request.getDateFrom(), request.getDateTo());
    } else {
      metrics = this.metricService.findAll(request.getName(), project.getId());
    }

    metrics.sort(Comparator.comparing(Metric::getDate));

    if (metrics.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(metrics);
    }

    MetricValueType metricValueType = this.metricService.getMetricValueTypeForList(metrics);
    if (metricValueType == null) {
      throw new MetricGetException("Metric value type is null");
    }

    switch (request.getOperation()) {
      case AVERAGE -> {
        return ResponseEntity.status(HttpStatus.OK).body(
            new MetricResponse<>(
                this.operationAverage(metrics),
                request.getName(),
                project.getId(),
                request.getOperation()
            )
        );
      }
      case SUM -> {
        return ResponseEntity.status(HttpStatus.OK).body(
            new MetricResponse<>(
                this.operationSum(metricValueType, metrics),
                request.getName(),
                project.getId(),
                request.getOperation()
            )
        );
      }
      case MAXIMUM -> {
        return ResponseEntity.status(HttpStatus.OK).body(
            new MetricResponse<>(
                this.operationMaximum(metricValueType, metrics),
                request.getName(),
                project.getId(),
                request.getOperation()
            )
        );
      }
      case NONE -> {
        return ResponseEntity.status(HttpStatus.OK).body(metrics);
      }
    }

    return ResponseEntity.status(HttpStatus.OK).body(metrics);
  }

  private Number operationSum(MetricValueType valueType, List<Metric> metrics) {
    return switch (valueType) {
      case DOUBLE -> {
        double sum = metrics.stream()
            .mapToDouble(Metric::getTypedValue)
            .sum();

        yield BigDecimal.valueOf(sum)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
      }
      case INTEGER -> (int) metrics.stream()
          .mapToLong(Metric::getTypedValue)
          .sum();
      default -> throw new MetricGetException("Operation SUM can't be invoked for this metric.");
    };
  }

  private Number operationMaximum(MetricValueType valueType, List<Metric> metrics) {
    return switch (valueType) {
      case DOUBLE -> metrics.stream()
          .map(metric -> (Double) metric.getTypedValue())
          .max(Comparator.comparing(Double::doubleValue))
          .orElse((double) 0);
      case INTEGER -> metrics.stream()
          .map(metric -> (Integer) metric.getTypedValue())
          .max(Comparator.comparing(Integer::intValue))
          .orElse(0);
      default -> throw new MetricGetException("Operation MAXIMUM can't be invoked for this metric.");
    };
  }

  private double operationAverage(List<Metric> metrics) {
    if (metrics.isEmpty()) {
      throw new MetricGetException("Cannot calculate average of an empty list.");
    }

    if (metrics.stream().anyMatch(metric -> metric.getValueType() != MetricValueType.INTEGER && metric.getValueType() != MetricValueType.DOUBLE)) {
      throw new MetricGetException("You want to average non Integer or Double type values.");
    }

    double sum = metrics.stream()
        .mapToDouble(metric -> {
          Object value = metric.getTypedValue();
          if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
          } else if (value instanceof Double) {
            return (Double) value;
          } else {
            throw new MetricGetException("Unexpected value type: " + value.getClass().getSimpleName());
          }
        })
        .sum();

    double average = sum / metrics.size();

    return BigDecimal.valueOf(average)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }

}
