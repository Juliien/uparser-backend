package fr.esgi.grp9.uparserbackend.user.service;

import fr.esgi.grp9.uparserbackend.authentication.login.Role;
import fr.esgi.grp9.uparserbackend.authentication.login.RoleRepository;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.domain.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService implements IUserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptEncoder;

    /**
     * Constructor Injection
     * better than @Autowired
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptEncoder = encoder;
    }

    @Override
    public User createUser(final User user) throws Exception {
        Role userRole = roleRepository.findByRole("USER");

        if (user.getEmail() == null || user.getFirstName() == null
                || user.getLastName() == null || user.getPassword() == null) {
            throw new Exception("Field can't be empty");
        }

        return userRepository.save(
                User.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .password(this.bCryptEncoder.encode(user.getPassword()))
                        .createDate(new Date())
                        .closeDate(null)
                        .lastLoginDate(new Date())
                        .roles(new HashSet<>(Arrays.asList(userRole)))
                        .build()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> _user = userRepository.findByEmail(email);
        if(_user.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }
            List<GrantedAuthority> authorities = getUserAuthority(_user.get().getRoles());
            return buildUserForAuthentication(_user.get(), authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        });

        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User updateUserPassword(User user) throws Exception {
        Optional<User> _currentUser =  userRepository.findByEmail(user.getEmail());
        if (_currentUser.isEmpty()) {
            throw new Exception("User doesn't exist!");
        }
        _currentUser.get().setPassword(this.bCryptEncoder.encode(user.getPassword()));

        return this.userRepository.save(_currentUser.get());
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public String getCode(String email) {
        String code = this.getStringRandom();
        User user = User.builder().email(email).password(code).build();
        try {
            this.updateUserPassword(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getStringRandom() {
        StringBuilder val = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 6; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if( "char".equalsIgnoreCase(charOrNum) ) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }
}
