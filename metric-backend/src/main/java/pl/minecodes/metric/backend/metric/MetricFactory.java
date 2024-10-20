package pl.minecodes.metric.backend.metric;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.stereotype.Component;
import pl.minecodes.metric.backend.metric.request.MetricReceiveRequest;
import pl.minecodes.metric.backend.sequence.SequenceService;
import pl.minecodes.metric.backend.util.AddressUtil;

@Component
class MetricFactory {

  private final SequenceService sequenceService;

  public MetricFactory(SequenceService sequenceService) {
    this.sequenceService = sequenceService;
  }

  public Metric createFromRequest(MetricReceiveRequest request, HttpServletRequest servletRequest) {
    return new Metric(
        this.sequenceService.generateSequence(Metric.SEQUENCE_NAME),
        request.getProjectId(),
        request.getName(),
        request.getValue(),
        Instant.now(),
        AddressUtil.getRealRequestAddress(servletRequest),
        request.getValueType()
    );
  }

}
