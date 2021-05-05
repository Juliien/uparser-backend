package fr.esgi.grp9.uparserbackend.user.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Constructor Injection
     * better than @Autowired
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(final User user) {
        return userRepository.save(
                User.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .phoneNumber(user.getPhoneNumber())
                        .birthDate(null)
                        .createDate(null)
                        .closeDate(null)
                        .lastLoginDate(null)
                        .token(null)
                        .roles(null)
                        .build()
        );
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
}
