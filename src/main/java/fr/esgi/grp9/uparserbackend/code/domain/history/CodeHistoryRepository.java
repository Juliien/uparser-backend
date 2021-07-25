package fr.esgi.grp9.uparserbackend.code.domain.history;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CodeHistoryRepository extends MongoRepository<CodeHistory, String> {
    List<CodeHistory> findAllByUserId(String userId);
    void deleteAllByUserId(String userId);
}
