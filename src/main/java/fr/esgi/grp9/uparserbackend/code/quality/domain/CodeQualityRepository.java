package fr.esgi.grp9.uparserbackend.code.quality.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeQualityRepository extends MongoRepository<Code, String> {
    Code findByCodeEncoded(String code);
    List<Code> findAllByUserId(String userId);
}
