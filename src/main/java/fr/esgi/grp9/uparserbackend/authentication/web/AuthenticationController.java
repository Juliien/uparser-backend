package fr.esgi.grp9.uparserbackend.authentication.web;

import fr.esgi.grp9.uparserbackend.authentication.login.LoginDTO;
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

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getPassword());

        authenticationManager.getObject().authenticate(authenticationToken);

        String token = tokenProvider.createToken(
                loginDTO.getEmail(),
                this.userService.findUserByEmail(loginDTO.getEmail()).getRoles());
        Map<String, String> result = new HashMap<>();
        result.put("email", loginDTO.getEmail());
        result.put("token", token);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }
}
