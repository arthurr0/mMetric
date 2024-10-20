package pl.minecodes.metric.backend.sequence;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "mm_sequences")
public class Sequence {

  private String id;
  private long sequence;

}