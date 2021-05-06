package fr.esgi.grp9.uparserbackend.user.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByFirstName(String username);
    Boolean existsByEmail(String email);
}
