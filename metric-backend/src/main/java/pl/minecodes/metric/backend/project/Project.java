package pl.minecodes.metric.backend.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "mm_projects")
public class Project {

  @Transient
  public static final String SEQUENCE_NAME = "projects_sequence";

  private final long id;
  private String name;
  private String integrationKey;

}
