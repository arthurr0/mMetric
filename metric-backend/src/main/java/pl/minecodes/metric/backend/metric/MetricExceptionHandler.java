package pl.minecodes.metric.backend.metric;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.minecodes.metric.backend.metric.exception.MetricGetException;
import pl.minecodes.metric.backend.metric.exception.MetricReceiveException;

@ControllerAdvice
class MetricExceptionHandler {

  @ExceptionHandler({ MetricReceiveException.class, MetricGetException.class })
  public ResponseEntity<?> handleException(RuntimeException exception) {
    Map<String, String> response = new HashMap<>();
    response.put("message", exception.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
  }
}
