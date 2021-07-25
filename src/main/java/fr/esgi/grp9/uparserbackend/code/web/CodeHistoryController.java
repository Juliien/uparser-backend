package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.history.CodeHistory;
import fr.esgi.grp9.uparserbackend.code.service.history.CodeHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/history")
public class CodeHistoryController {
    private final CodeHistoryService codeHistoryService;

    public CodeHistoryController(CodeHistoryService codeHistoryService) {
        this.codeHistoryService = codeHistoryService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<CodeHistory>> getCodeHistoryByUser(@PathVariable String id) {
        if(id != null) {
            try {
                return new ResponseEntity<>( this.codeHistoryService.getUserCodeHistory(id), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<CodeHistory> addCodeHistory(@RequestBody CodeHistory codeHistory) {
        try {
            return new ResponseEntity<>( this.codeHistoryService.addCodeHistory(codeHistory), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCodeHistory(@PathVariable String id) {
        this.codeHistoryService.deleteCodeHistory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAllUserCodeHistory(@PathVariable String userId) {
        this.codeHistoryService.deleteUserCodeHistory(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
