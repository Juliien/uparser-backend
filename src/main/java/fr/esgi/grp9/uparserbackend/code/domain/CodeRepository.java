package fr.esgi.grp9.uparserbackend.code.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends MongoRepository<Code, String> {
    Optional<Code> findByHash(String code);
    List<Code> findAllByUserId(String userId);
    List<Code> findAllByEnableIs(boolean enable);
}
