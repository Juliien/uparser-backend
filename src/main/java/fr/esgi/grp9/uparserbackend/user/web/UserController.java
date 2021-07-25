package fr.esgi.grp9.uparserbackend.user.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        if (email == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty email.");
        }
        Optional<User> _user;
        try {
            _user = this.userService.findUserByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (_user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't exist.");
        }
        return new ResponseEntity<>(_user.get(), HttpStatus.OK);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable String id){
//        if (id == null){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
//        }
//        Optional<User> _user;
//        try {
//            _user = userService.findUserById(id);
//        } catch (Exception e){
//            e.printStackTrace();
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//        if (_user.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't exist.");
//        }
//        return new ResponseEntity<>(_user.get(), HttpStatus.OK);
//    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        try {
            return new ResponseEntity<>(this.userService.getUsers(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<User> updateUserPassword(@RequestBody User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incomplete user.");
        }
        try {
            return new ResponseEntity<>(this.userService.updateUserPassword(user), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
