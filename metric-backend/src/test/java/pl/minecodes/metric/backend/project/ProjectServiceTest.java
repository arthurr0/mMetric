package pl.minecodes.metric.backend.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.minecodes.metric.backend.project.exception.ProjectUpdateException;
import pl.minecodes.metric.backend.project.request.ProjectUpdateRequest;
import pl.minecodes.metric.backend.util.KeyUtil;

@SpringBootTest
@Testcontainers
class ProjectServiceTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

  @Autowired
  private ProjectFactory projectFactory;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @AfterEach
  void cleanup() {
    this.mongoTemplate.getDb().drop();
  }

  @Test
  void findById() {
    Project project = this.projectFactory.createFromName("mMetricId");
    this.projectService.save(project);

    assertThat(this.projectService.findById(project.getId())).isPresent();
  }

  @Test
  void findNotExistById() {
    assertThat(this.projectService.findById(99L)).isEmpty();
  }

  @Test
  void findByName() {
    Project project = this.projectFactory.createFromName("mMetricName");
    this.projectService.save(project);

    assertThat(this.projectService.findByName(project.getName())).isPresent();
  }

  @Test
  void findNotExistByName() {
    assertThat(this.projectService.findByName("mMetricNotExist")).isEmpty();
  }

  @Test
  void updateName() {
    Project project = this.projectFactory.createFromName("mMetricUpdate");
    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), "mMetricUpdated", null);

    Project updated = this.projectService.update(project, request);

    assertThat(updated.getName()).isEqualTo("mMetricUpdated");
  }

  @Test
  void updateNameWithSame() {
    Project project = this.projectFactory.createFromName("mMetricUpdate");
    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), project.getName(), null);

    assertThatThrownBy(() -> {
      this.projectService.update(project, request);
    }).isInstanceOf(ProjectUpdateException.class)
        .hasMessage("Project already have this name.");
  }

  @Test
  void updateIntegrationKey() {
    Project project = this.projectFactory.createFromName("mMetricUpdateKey");
    this.projectService.save(project);

    String integrationKey = KeyUtil.keyGenerator();

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), null, integrationKey);

    Project updated = this.projectService.update(project, request);

    assertThat(updated.getIntegrationKey()).isEqualTo(integrationKey);
  }

  @Test
  void updateIntegrationKeyWithSameKey() {
    Project project = this.projectFactory.createFromName("mMetricUpdateKeySame");
    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), null, project.getIntegrationKey());

    assertThatThrownBy(() -> {
      this.projectService.update(project, request);
    }).isInstanceOf(ProjectUpdateException.class)
        .hasMessage("Project already have this integration key.");
  }


  @Test
  void updateIntegrationKeyWithBadKey() {
    Project project = this.projectFactory.createFromName("mMetricUpdateKeyBad");
    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), null, "abcd");

    assertThatThrownBy(() -> {
      this.projectService.update(project, request);
    }).isInstanceOf(ProjectUpdateException.class)
        .hasMessage("Integration key need to have minimum 32 chars.");
  }

  @Test
  void updateWithoutData() {
    Project project = this.projectFactory.createFromName("mMetricUpdateKey");
    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), null, null);

    assertThatThrownBy(() -> {
      this.projectService.update(project, request);
    }).isInstanceOf(ProjectUpdateException.class)
        .hasMessage("Request don't have any data to update!");
  }

  @Test
  void isExists() {
    Project project = this.projectFactory.createFromName("mMetricExist");
    this.projectService.save(project);

    assertThat(this.projectService.exists("mMetricExist")).isEqualTo(true);
  }


  @Test
  void isExistsNoData() {
    assertThat(this.projectService.exists("mMetricExist")).isEqualTo(false);
  }

  @Test
  void saveProject() {
    this.projectService.save(this.projectFactory.createFromName("mMetricSave"));

    assertThat(this.projectService.findByName("mMetricSave")).isPresent();
  }



}
