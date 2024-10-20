package pl.minecodes.metric.backend.project;

import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.minecodes.metric.backend.project.exception.ProjectUpdateException;
import pl.minecodes.metric.backend.project.request.ProjectUpdateRequest;

@Service
public class ProjectService {

  private final ProjectRepository repository;

  public ProjectService(ProjectRepository repository) {
    this.repository = repository;
  }

  public Optional<Project> findById(long id) {
    return this.repository.findById(id);
  }

  public Optional<Project> findByName(String name) {
    return this.repository.findByName(name);
  }

  public Optional<Project> findByIntegrationKey(String integrationKey){
    return this.repository.findByIntegrationKey(integrationKey);
  }

  public boolean exists(long id) {
    return this.repository.countById(id) > 0;
  }

  public boolean exists(String name) {
    return this.repository.countByName(name) > 0;
  }

  public Project update(Project project, ProjectUpdateRequest request) {
    if (request.getName() == null && request.getIntegrationKey() == null) {
      throw new ProjectUpdateException("Request don't have any data to update!");
    }

    if (request.getName() != null) {
      if (request.getName().equals(project.getName())) {
        throw new ProjectUpdateException("Project already have this name.");
      }

      project.setName(request.getName());
    }

    if (request.getIntegrationKey() != null) {
      if (request.getIntegrationKey().length() < 32) {
        throw new ProjectUpdateException("Integration key need to have minimum 32 chars.");
      }

      if (request.getIntegrationKey().equals(project.getIntegrationKey())) {
        throw new ProjectUpdateException("Project already have this integration key.");
      }

      project.setIntegrationKey(request.getIntegrationKey());
    }

    this.save(project);

    return project;
  }

  public void save(Project project) {
    this.repository.save(project);
  }

  public void delete(Project project) {
    this.repository.delete(project);
  }
}
