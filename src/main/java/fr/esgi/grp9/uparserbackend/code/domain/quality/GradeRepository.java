package fr.esgi.grp9.uparserbackend.code.domain.quality;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository  extends MongoRepository<Grade, String> {
    Optional<List<Grade>> findAllByCodeId(String codeId);
    Optional<List<Grade>> findAllByRunId(String runId);
    Optional<Grade> findById(String id);
}
