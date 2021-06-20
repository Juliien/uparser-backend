package fr.esgi.grp9.uparserbackend.run.web;

import fr.esgi.grp9.uparserbackend.common.exception.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunService;
import fr.esgi.grp9.uparserbackend.user.domain.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/run")
public class RunController {
    private final RunService runService;
    private final UserService userService;
//    private final FileService fileService;

    public RunController(RunService runService, UserService userService
//                        , FileService fileService
    ) {
        this.runService = runService;
        this.userService = userService;
//        this.fileService = fileService;
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
            //TODO faire en sorte que get by email tthrow une erreur
            userService.findUserByEmail(run.getUserEmail());
//            fileService.findFileById(run.getFileId());
            Run _run = runService.createRun(run);
            return new ResponseEntity<>(_run, HttpStatus.CREATED);
        } catch (Exception exception){
            //TODO mettre dans la reponse une explication de ce qu'il manque
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Run> modifyRun(@RequestBody final Run run){
        try {
            runService.findRunById(run.getId());
            return new ResponseEntity<>(runService.modifyRun(run), HttpStatus.OK);
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
