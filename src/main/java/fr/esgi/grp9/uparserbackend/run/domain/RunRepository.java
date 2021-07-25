package fr.esgi.grp9.uparserbackend.run.domain;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RunRepository extends MongoRepository<Run, String> {
    Optional<Run> findById(String id);
    Optional<List<Run>> findAllByUserId(String userId);
    Optional<List<Run>> findAllByCodeId(String codeId);
}
