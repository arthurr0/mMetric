package pl.minecodes.metric.backend.project;

import static org.assertj.core.api.Assertions.assertThat;

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
import pl.minecodes.metric.backend.project.request.ProjectCreateRequest;

@SpringBootTest
@Testcontainers
class ProjectFactoryTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

  @Autowired
  private ProjectFactory projectFactory;

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
  void createFromRequest() {
    ProjectCreateRequest request = new ProjectCreateRequest("mMetricRequest");
    Project project = this.projectFactory.createFromRequest(request);

    assertThat(project.getName()).isEqualTo("mMetricRequest");
    assertThat(project.getId()).isEqualTo(1);
    assertThat(project.getIntegrationKey()).isNotEmpty();
  }

  @Test
  void createFromName() {
    Project project = this.projectFactory.createFromName("mMetricName");

    assertThat(project.getName()).isEqualTo("mMetricName");
    assertThat(project.getId()).isEqualTo(1);
    assertThat(project.getIntegrationKey()).isNotEmpty();
  }

}
