package fr.esgi.grp9.uparserbackend.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User findUserByEmail(String email);
    List<User> getUsers();
}
