package fr.esgi.grp9.uparserbackend.authentication.web;

import fr.esgi.grp9.uparserbackend.authentication.login.LoginDTO;
import fr.esgi.grp9.uparserbackend.authentication.login.LoginResponseDTO;
import fr.esgi.grp9.uparserbackend.authentication.security.TokenProvider;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.domain.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManager;
    private final UserServiceImpl userService;

    public AuthenticationController(TokenProvider tokenProvider,
                                    AuthenticationManagerBuilder authenticationManager,
                                    UserServiceImpl userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        if(loginDTO.getEmail() != null && loginDTO.getPassword() != null
                && !loginDTO.getEmail().isBlank() && !loginDTO.getPassword().isBlank()
                && !loginDTO.getEmail().isEmpty() && !loginDTO.getPassword().isEmpty()) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword());

            authenticationManager.getObject().authenticate(authenticationToken);

            String token = tokenProvider.createToken(
                    this.userService.findUserByEmail(loginDTO.getEmail()));

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody final User user) {

        if(user.getEmail() != null) {
            User _userExist = userService.findUserByEmail(user.getEmail());

            if(_userExist == null) {
                try {
                    User _user = userService.createUser(user);
                    if(_user != null) {
                        return new ResponseEntity<>(_user, HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
