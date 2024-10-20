package pl.minecodes.metric.backend.sequence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class SequenceServiceTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

  @Autowired
  private SequenceService sequenceService;

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
  void generateSequence() {
    long mMetric = this.sequenceService.generateSequence("mMetric");
    assertThat(mMetric).isEqualTo(1L);
  }

  @Test
  void generateFewSequences() {
    long mMetricOne = this.sequenceService.generateSequence("mMetric");
    assertThat(mMetricOne).isEqualTo(1L);

    long mMetricTwo = this.sequenceService.generateSequence("mMetric");
    assertThat(mMetricTwo).isEqualTo(2L);
  }
}
