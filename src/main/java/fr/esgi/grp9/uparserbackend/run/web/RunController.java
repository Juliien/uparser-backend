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
        try {
            Optional<Run> _run = runService.findRunById(id);
            if (_run.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This run doesn't exist.");
            }
            return new ResponseEntity<>(_run.get(), HttpStatus.OK);
        } catch (Exception exception){
            exception.printStackTrace();
            throw  new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRun(@PathVariable String id) {
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        try {
            Optional<Run> _run = runService.findRunById(id);
            if (_run.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This run doesn't exist.");
            }
            runService.deleteRunById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

}
