package fr.esgi.grp9.uparserbackend.historic.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricRepository extends MongoRepository<Historic, String> {
}
