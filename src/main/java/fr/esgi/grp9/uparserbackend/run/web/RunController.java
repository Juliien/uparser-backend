package fr.esgi.grp9.uparserbackend.run.web;

import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.service.IRunService;
import fr.esgi.grp9.uparserbackend.user.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/runs")
public class RunController {
    private final IRunService runService;
    private final IUserService userService;

    public RunController(IRunService runService, IUserService userService) {
        this.runService = runService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Run>> getRuns() {
        return new ResponseEntity<>(this.runService.getRuns(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> getRunById(@PathVariable String id){
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<Run> _run;
        try {
            _run = runService.findRunById(id);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        if (_run.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This run doesn't exist.");
        }
        return new ResponseEntity<>(_run.get(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Run>> getRunsByUserId(@PathVariable String id) {
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<List<Run>> _runs;
        try {
            _runs = this.runService.getRunsByUserId(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (_runs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't exist.");
        }
        return new ResponseEntity<>(_runs.get(), HttpStatus.OK);
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<List<Run>> getRunsByCodeId(@PathVariable String id) {
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<List<Run>> _runs;
        try {
            _runs = this.runService.getRunsByCodeId(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (_runs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This code doesn't exist.");
        }
        return new ResponseEntity<>(_runs.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Run> createRun(@RequestBody final Run run) {
        if (userService.findUserById(run.getUserId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This run doesn't exist.");
        }
        try {
            return new ResponseEntity<>(runService.createRun(run), HttpStatus.CREATED);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRun(@PathVariable String id) {
        getRunById(id);
        try {
            runService.deleteRunById(id);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
