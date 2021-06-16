package fr.esgi.grp9.uparserbackend.user.domain;

import java.util.List;

public interface UserService {
    User createUser(User user) throws Exception;
    User findUserByEmail(String email);
    List<User> getUsers();
    User updateUser(User user);
}
