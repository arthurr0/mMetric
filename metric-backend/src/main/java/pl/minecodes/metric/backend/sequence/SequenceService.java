package pl.minecodes.metric.backend.sequence;

import java.util.Objects;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

  private final MongoOperations mongoOperations;

  public SequenceService(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public long generateSequence(String sequenceName) {
    Sequence counter = mongoOperations.findAndModify(
        Query.query(Criteria.where("_id").is(sequenceName)),
        new Update().inc("sequence", 1),
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        Sequence.class
    );

    return !Objects.isNull(counter) ? counter.getSequence() : 1;
  }
}