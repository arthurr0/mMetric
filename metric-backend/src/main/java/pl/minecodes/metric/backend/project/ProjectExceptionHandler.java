package pl.minecodes.metric.backend.project;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.minecodes.metric.backend.project.exception.ProjectCreateException;
import pl.minecodes.metric.backend.project.exception.ProjectNotFoundException;
import pl.minecodes.metric.backend.project.exception.ProjectUpdateException;

@ControllerAdvice
class ProjectExceptionHandler {

  @ExceptionHandler({ ProjectCreateException.class, ProjectUpdateException.class })
  public ResponseEntity<?> handleException(RuntimeException exception) {
    Map<String, String> response = new HashMap<>();
    response.put("message", exception.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
  }

  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<?> handleNotFoundException(RuntimeException exception) {
    Map<String, String> response = new HashMap<>();
    response.put("message", exception.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
  }

}
