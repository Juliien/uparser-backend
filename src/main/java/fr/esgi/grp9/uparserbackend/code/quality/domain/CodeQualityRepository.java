package fr.esgi.grp9.uparserbackend.code.quality.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeQualityRepository extends MongoRepository<Code, String> {
}
