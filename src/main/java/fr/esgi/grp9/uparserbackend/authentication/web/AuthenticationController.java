package fr.esgi.grp9.uparserbackend.authentication.web;

import fr.esgi.grp9.uparserbackend.authentication.login.LoginDTO;
import fr.esgi.grp9.uparserbackend.authentication.login.LoginResponseDTO;
import fr.esgi.grp9.uparserbackend.authentication.security.TokenProvider;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManager;
    private final UserService userService;

    public AuthenticationController(TokenProvider tokenProvider,
                                    AuthenticationManagerBuilder authenticationManager,
                                    UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {

        if(loginDTO.getEmail() == null || loginDTO.getPassword() == null
                || loginDTO.getEmail().isBlank() || loginDTO.getPassword().isBlank()
                || loginDTO.getEmail().isEmpty() || loginDTO.getPassword().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some fields are empty.");
        }

        Optional<User> _user = this.userService.findUserByEmail(loginDTO.getEmail());

        if (_user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't exist.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getPassword());

        String _token = tokenProvider.createToken(_user.get());

        authenticationManager.getObject().authenticate(authenticationToken);


        LoginResponseDTO response = LoginResponseDTO.builder().token(_token).build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody final User user) {

        if(user.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some fields are empty.");
        }

        Optional<User> _user = this.userService.findUserByEmail(user.getEmail());

        if(_user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists !");
        }

        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
