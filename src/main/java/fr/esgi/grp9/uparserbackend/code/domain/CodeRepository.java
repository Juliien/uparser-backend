package fr.esgi.grp9.uparserbackend.code.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends MongoRepository<Code, String> {
    List<Code> findAllByHash(String code);
    List<Code> findAllByUserId(String userId);
}
