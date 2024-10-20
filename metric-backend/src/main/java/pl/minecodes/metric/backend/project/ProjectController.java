package pl.minecodes.metric.backend.project;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.minecodes.metric.backend.project.exception.ProjectCreateException;
import pl.minecodes.metric.backend.project.exception.ProjectNotFoundException;
import pl.minecodes.metric.backend.project.request.ProjectCreateRequest;
import pl.minecodes.metric.backend.project.request.ProjectUpdateRequest;

@RestController
@RequestMapping("/api/v1/project")
class ProjectController {

  private final ProjectFactory projectFactory;
  private final ProjectService projectService;

  public ProjectController(ProjectFactory projectFactory, ProjectService projectService) {
    this.projectFactory = projectFactory;
    this.projectService = projectService;
  }

  @PutMapping
  public ResponseEntity<?> createProject(@RequestBody ProjectCreateRequest request) {
    if (this.projectService.exists(request.getName())) {
      throw new ProjectCreateException("Project with this name is already exists.");
    }

    Project project = this.projectFactory.createFromRequest(request);

    this.projectService.save(project);

    return ResponseEntity.status(HttpStatus.CREATED).body(project);
  }

  @PostMapping
  public ResponseEntity<?> updateProject(@RequestBody ProjectUpdateRequest request) {
    Optional<Project> projectOptional = this.projectService.findById(request.getId());
    if (projectOptional.isEmpty()) {
      throw new ProjectNotFoundException("Project with provided id is not exists.");
    }

    Project project = projectOptional.get();

    return ResponseEntity.status(HttpStatus.OK).body(this.projectService.update(project, request));
  }

  @GetMapping("name")
  public ResponseEntity<?> getProjectByName(@RequestParam String name) {
    Optional<Project> projectOptional = this.projectService.findByName(name);
    if (projectOptional.isEmpty()) {
      throw new ProjectNotFoundException("Project with provided name is not exists.");
    }

    Project project = projectOptional.get();

    return ResponseEntity.status(HttpStatus.OK).body(project);
  }

  @GetMapping("id")
  public ResponseEntity<?> getProjectById(@RequestParam long id) {
    Optional<Project> projectOptional = this.projectService.findById(id);
    if (projectOptional.isEmpty()) {
      throw new ProjectNotFoundException("Project with provided id is not exists.");
    }

    Project project = projectOptional.get();

    return ResponseEntity.status(HttpStatus.OK).body(project);
  }

  @DeleteMapping
  public ResponseEntity<?> deleteProject(@RequestParam long id) {
    Optional<Project> projectOptional = this.projectService.findById(id);
    if (projectOptional.isEmpty()) {
      throw new ProjectNotFoundException("Project with provided id is not exists.");
    }

    Project project = projectOptional.get();

    this.projectService.delete(project);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
