package fr.esgi.grp9.uparserbackend.run.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunRepository extends MongoRepository<Run, String> {
    Optional<Run> findById(String id);
    void deleteById(String id);
}
