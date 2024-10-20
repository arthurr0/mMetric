package pl.minecodes.metric.backend.project;

import org.springframework.stereotype.Component;
import pl.minecodes.metric.backend.project.request.ProjectCreateRequest;
import pl.minecodes.metric.backend.sequence.SequenceService;
import pl.minecodes.metric.backend.util.KeyUtil;

@Component
class ProjectFactory {

  private final SequenceService sequenceService;

  public ProjectFactory(SequenceService sequenceService) {
    this.sequenceService = sequenceService;
  }

  public Project createFromRequest(ProjectCreateRequest request) {
    return new Project(
        this.sequenceService.generateSequence(Project.SEQUENCE_NAME),
        request.getName(),
        KeyUtil.keyGenerator()
    );
  }

  public Project createFromName(String name) {
    return new Project(
        this.sequenceService.generateSequence(Project.SEQUENCE_NAME),
        name,
        KeyUtil.keyGenerator()
    );
  }

}
