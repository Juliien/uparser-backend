package fr.esgi.grp9.uparserbackend.user.service;

import fr.esgi.grp9.uparserbackend.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User createUser(User user) throws Exception;
    User updateUserPassword(User user) throws Exception;
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(String id);
    List<User> getUsers();
    String getCode(String email);
    void deleteUserById(String id);
}