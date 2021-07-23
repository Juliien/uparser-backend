package fr.esgi.grp9.uparserbackend.file.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String>{
    Optional<File> findById(String id);
    List<File> findAllByUserId(String userId);
}