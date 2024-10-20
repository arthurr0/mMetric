package pl.minecodes.metric.backend.metric;

import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

interface MetricRepository extends MongoRepository<Metric, Long> {

  @Query(value = "{ 'name' : ?0, 'projectId' : ?1 }")
  List<Metric> findAll(String name, long projectId);

  @Query(value = "{ 'name' : ?0, 'projectId' : ?1, 'date': {$gte: ?2, $lte: ?3}}")
  List<Metric> findAllBetweenDates(String name, long projectId, Instant from, Instant to);

}
