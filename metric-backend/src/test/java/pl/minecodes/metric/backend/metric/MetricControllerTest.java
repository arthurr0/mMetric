package pl.minecodes.metric.backend.metric;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.minecodes.metric.backend.metric.request.MetricGetRequest;
import pl.minecodes.metric.backend.metric.request.MetricReceiveRequest;
import pl.minecodes.metric.backend.project.Project;
import pl.minecodes.metric.backend.project.ProjectService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MetricControllerTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MetricService metricService;
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
  void receiveNoMetricsRequest() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));

    List<MetricReceiveRequest> metrics = new ArrayList<>();

    this.mockMvc.perform(put("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(metrics)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Metrics are empty."));
  }

  @Test
  void receiveOneMetricRequestWithoutProject() throws Exception {
    List<MetricReceiveRequest> metrics = List.of(new MetricReceiveRequest(
        1,
        "views",
        "10",
        MetricValueType.INTEGER
    ));

    this.mockMvc.perform(put("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(metrics)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Provided project id is not exists."));
  }

  @Test
  void receiveOneMetricRequest() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));

    List<MetricReceiveRequest> metrics = List.of(new MetricReceiveRequest(
        1,
        "views",
        "10",
        MetricValueType.INTEGER
    ));

    this.mockMvc.perform(put("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(metrics)))
        .andExpect(status().isCreated());
  }

  @Test
  void receiveMoreMetricsRequest() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));

    List<MetricReceiveRequest> metrics = List.of(
        new MetricReceiveRequest(
            1,
            "views",
            "10",
            MetricValueType.INTEGER
        ),
        new MetricReceiveRequest(
            1,
            "version",
            "v1.1",
            MetricValueType.STRING
        )
    );

    this.mockMvc.perform(put("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(metrics)))
        .andExpect(status().isCreated());
  }

  @Test
  void receiveOneMetricWithDifferentValueType() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "version", "v1.2", Instant.now(), "0.0.0.0", MetricValueType.STRING));

    List<MetricReceiveRequest> metrics = List.of(
        new MetricReceiveRequest(
            1,
            "version",
            "v1.1",
            MetricValueType.INTEGER
        )
    );

    this.mockMvc.perform(put("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(metrics)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("The specified value type for (version) is different than before. Please use STRING."));
  }

  @Test
  void getMetricWithoutProject() throws Exception {
    MetricGetRequest request = new MetricGetRequest(
        1L,
        "version",
        MetricOperation.NONE,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Project with provided id is not exists."));
  }

  @Test
  void getEmptyMetrics() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "version",
        MetricOperation.NONE,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void getMetricsWithOperationNone() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "version", "v1.2", Instant.now(), "0.0.0.0", MetricValueType.STRING));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "version",
        MetricOperation.NONE,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists());
  }

  @Test
  void getMetricsWithOperationAverage() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "views", "10", Instant.now(), "0.0.0.0", MetricValueType.INTEGER));
    this.metricService.save(new Metric(2L, 1L, "views", "20", Instant.now().plusSeconds(60), "0.0.0.0", MetricValueType.INTEGER));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "views",
        MetricOperation.AVERAGE,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.value").value(15.0))
        .andExpect(jsonPath("$.name").value("views"))
        .andExpect(jsonPath("$.projectId").value(1))
        .andExpect(jsonPath("$.operation").value("AVERAGE"));
  }

  @Test
  void getMetricsWithOperationSum() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "revenue", "100.50", Instant.now(), "0.0.0.0", MetricValueType.DOUBLE));
    this.metricService.save(new Metric(2L, 1L, "revenue", "200.75", Instant.now().plusSeconds(60), "0.0.0.0", MetricValueType.DOUBLE));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "revenue",
        MetricOperation.SUM,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.value").value(301.25))
        .andExpect(jsonPath("$.name").value("revenue"))
        .andExpect(jsonPath("$.projectId").value(1))
        .andExpect(jsonPath("$.operation").value("SUM"));
  }

  @Test
  void getMetricsWithOperationMaximum() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "temperature", "25.5", Instant.now(), "0.0.0.0", MetricValueType.DOUBLE));
    this.metricService.save(new Metric(2L, 1L, "temperature", "30.2", Instant.now().plusSeconds(60), "0.0.0.0", MetricValueType.DOUBLE));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "temperature",
        MetricOperation.MAXIMUM,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.value").value(30.2))
        .andExpect(jsonPath("$.name").value("temperature"))
        .andExpect(jsonPath("$.projectId").value(1))
        .andExpect(jsonPath("$.operation").value("MAXIMUM"));
  }

  @Test
  void getMetricsWithDateRange() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    Instant now = Instant.now();
    this.metricService.save(new Metric(1L, 1L, "visitors", "100", now.minusSeconds(3600), "0.0.0.0", MetricValueType.INTEGER));
    this.metricService.save(new Metric(2L, 1L, "visitors", "200", now, "0.0.0.0", MetricValueType.INTEGER));
    this.metricService.save(new Metric(3L, 1L, "visitors", "300", now.plusSeconds(3600), "0.0.0.0", MetricValueType.INTEGER));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "visitors",
        MetricOperation.NONE,
        now.minusSeconds(1800),
        now.plusSeconds(1800)
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].value").value("200"));
  }

  @Test
  void getMetricsWithInvalidOperation() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));
    this.metricService.save(new Metric(1L, 1L, "version", "v1.0", Instant.now(), "0.0.0.0", MetricValueType.STRING));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "version",
        MetricOperation.SUM,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Operation SUM can't be invoked for this metric."));
  }

  @Test
  void getMetricsWithEmptyResult() throws Exception {
    this.projectService.save(new Project(1L, "mMetric", "key"));

    MetricGetRequest request = new MetricGetRequest(
        1L,
        "nonexistent",
        MetricOperation.NONE,
        null,
        null
    );

    this.mockMvc.perform(get("/api/v1/metric")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
