package pl.minecodes.metric.backend.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.minecodes.metric.backend.project.request.ProjectCreateRequest;
import pl.minecodes.metric.backend.project.request.ProjectUpdateRequest;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProjectControllerTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProjectService projectService;
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
  void testCreateProject() throws Exception {
    ProjectCreateRequest request = new ProjectCreateRequest("mMetric");

    this.mockMvc.perform(put("/api/v1/project")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("mMetric"))
        .andExpect(jsonPath("$.integrationKey").exists());
  }

  @Test
  void testCreateProjectAlreadyExists() throws Exception {
    ProjectCreateRequest request = new ProjectCreateRequest("mMetric");

    Project existingProject = this.projectFactory.createFromRequest(request);
    this.projectService.save(existingProject);

    this.mockMvc.perform(put("/api/v1/project")
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateProject() throws Exception {
    Project project = this.projectFactory.createFromName("mMetricUpdate");

    this.projectService.save(project);

    ProjectUpdateRequest request = new ProjectUpdateRequest(project.getId(), "mMetricUpdated", null);

    this.mockMvc.perform(post("/api/v1/project")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("mMetricUpdated"));
  }

  @Test
  void testUpdateProjectNotFound() throws Exception {
    ProjectUpdateRequest request = new ProjectUpdateRequest(999L, "New Name", null);

    this.mockMvc.perform(post("/api/v1/project")
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetProjectByName() throws Exception {
    Project project = this.projectFactory.createFromName("mMetricGetName");

    this.projectService.save(project);

    this.mockMvc.perform(get("/api/v1/project/name")
            .param("name", "mMetricGetName"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("mMetricGetName"));
  }

  @Test
  void testGetProjectByNameNotFound() throws Exception {
    this.mockMvc.perform(get("/api/v1/project/name")
            .param("name", "mMetricGetNameNotFound"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetProjectById() throws Exception {
    Project project = this.projectFactory.createFromName("mMetricGetId");

    this.projectService.save(project);

    this.mockMvc.perform(get("/api/v1/project/id")
            .param("id", String.valueOf(project.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("mMetricGetId"));
  }

  @Test
  void testGetProjectByIdNotFound() throws Exception {
    this.mockMvc.perform(get("/api/v1/project/id")
            .param("id", "999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteProject() throws Exception {
    Project project = this.projectFactory.createFromName("mMetricDelete");

    this.projectService.save(project);

    this.mockMvc.perform(delete("/api/v1/project")
            .param("id", String.valueOf(project.getId())))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteProjectNotFound() throws Exception {
    this.mockMvc.perform(delete("/api/v1/project")
        .param("id", "999"))
        .andExpect(status().isNotFound());
  }
}