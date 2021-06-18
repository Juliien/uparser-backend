package fr.esgi.grp9.uparserbackend.user.web;

import fr.esgi.grp9.uparserbackend.user.domain.User;
import fr.esgi.grp9.uparserbackend.user.domain.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        if(email != null) {
            try {
                User user = this.userService.findUserByEmail(email);
                if(user != null)  {
                    return new ResponseEntity<>(user , HttpStatus.OK);
                }

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        try {
            return new ResponseEntity<>(this.userService.getUsers(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<User> updateUserPassword(@RequestBody User user) {
        try {
            User updatedUser = this.userService.updateUserPassword(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
