package fr.esgi.grp9.uparserbackend.file.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String>{
<<<<<<< HEAD
    Optional<File> findById(String id);
    void deleteById(String id);
=======
//    File findFileById(String id);
    Optional<File> findFileById(String id);
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
}
