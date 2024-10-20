package pl.minecodes.metric.backend.project.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {

  private long id;
  private String name;
  private String integrationKey;

}
