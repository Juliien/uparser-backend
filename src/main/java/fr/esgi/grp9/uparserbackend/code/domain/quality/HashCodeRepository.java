package fr.esgi.grp9.uparserbackend.code.domain.quality;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashCodeRepository extends MongoRepository<HashCode, String> {
    Optional<HashCode> findById(String id);
}
