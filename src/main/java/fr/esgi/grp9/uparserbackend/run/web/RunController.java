package fr.esgi.grp9.uparserbackend.run.web;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunService;
import fr.esgi.grp9.uparserbackend.user.domain.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/runs")
public class RunController {
    private final RunService runService;
    private final UserService userService;

    public RunController(RunService runService, UserService userService) {
        this.runService = runService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Run>> getRuns() {
        return new ResponseEntity<>(this.runService.getRuns(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> getRunById(@PathVariable String id){
        try {
            return new ResponseEntity<>(runService.findRunById(id), HttpStatus.OK);
        } catch (NotFoundWithIdException notFoundWithIdException){
            System.out.println(notFoundWithIdException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Run> createRun(@RequestBody final Run run) {
        try {
            return new ResponseEntity<>(runService.createRun(run), HttpStatus.CREATED);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Run> updateRun(@RequestBody final Run run){
        try {
            runService.findRunById(run.getId());
            return new ResponseEntity<>(runService.updateRun(run), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRun(@PathVariable String id) {
        Run _runExist = runService.findRunById(id);

        if (_runExist != null) {
            try {
                runService.deleteRunById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception exception) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
