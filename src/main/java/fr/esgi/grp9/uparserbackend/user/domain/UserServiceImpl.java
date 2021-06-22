package fr.esgi.grp9.uparserbackend.user.domain;

import fr.esgi.grp9.uparserbackend.authentication.login.Role;
import fr.esgi.grp9.uparserbackend.authentication.login.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptEncoder;

    /**
     * Constructor Injection
     * better than @Autowired
     */
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptEncoder = encoder;
    }

    @Override
    public User createUser(final User user) throws Exception {
        Role userRole = roleRepository.findByRole("USER");

        if (user.getEmail() != null && user.getFirstName() != null
                && user.getLastName() != null && user.getPassword() != null) {
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
        } else {
            throw new Exception("Field can't be empty");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException("Email not found");
        }
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
    public User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User updateUserPassword(User user) throws Exception {
        User currentUser =  userRepository.findByEmail(user.getEmail());
        if(currentUser != null) {
            currentUser.setPassword(this.bCryptEncoder.encode(user.getPassword()));
            return this.userRepository.save(currentUser);
        } else {
            throw new Exception("User doesn't exist!");
        }
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public String getCode(String email) {
        String code = this.getStringRandom(6);
        User user = User.builder().email(email).password(code).build();
        try {
            this.updateUserPassword(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if( "char".equalsIgnoreCase(charOrNum) ) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }
}
