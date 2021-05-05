package fr.esgi.grp9.uparserbackend.authentication.login;

import fr.esgi.grp9.uparserbackend.authentication.security.TokenProvider;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.domain.UserRepository;
import fr.esgi.grp9.uparserbackend.user.domain.UserServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());

        Authentication authentication = authenticationManager.getObject().authenticate(authenticationToken);

        String token = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.OK);
    }
}
