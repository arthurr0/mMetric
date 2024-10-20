package pl.minecodes.metric.backend.project;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

interface ProjectRepository extends MongoRepository<Project, Long> {

  Optional<Project> findById(long id);

  Optional<Project> findByName(String name);

  @Query(value = "{ 'name' : ?0 }", count = true)
  int countByName(String name);

  @Query(value = "{ '_id' : ?0 }", count = true)
  int countById(long id);
}
