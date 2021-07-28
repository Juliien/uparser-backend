package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;
import fr.esgi.grp9.uparserbackend.code.service.quality.IGradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradeController {
    private final IGradeService gradeService;

    public GradeController(IGradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getGrades() {
        return new ResponseEntity<>(this.gradeService.getGrades(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable String id){
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<Grade> _grade;
        try {
            _grade = this.gradeService.findGradeById(id);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        if (_grade.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This grade doesn't exist.");
        }
        return new ResponseEntity<>(_grade.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable String id) {
        getGradeById(id);
        try {
            this.gradeService.deleteGradeById(id);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
