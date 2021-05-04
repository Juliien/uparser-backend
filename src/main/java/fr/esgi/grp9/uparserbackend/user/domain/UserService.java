package fr.esgi.grp9.uparserbackend.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User addUser(User user);
    List<User> getUsers();
    Optional<User> getUserById(String id);
}
