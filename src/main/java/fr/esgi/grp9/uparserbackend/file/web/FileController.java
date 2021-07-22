package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<File>> getFiles() {
        return new ResponseEntity<>(this.fileService.getFiles(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<File>> getFilesByUserId(@PathVariable String id) {
        return new ResponseEntity<>(this.fileService.getFilesByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id){
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        try {
            Optional<File> _file = fileService.findFileById(id);
            if (_file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This file doesn't exist.");
            }

            return new ResponseEntity<>(_file.get(), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<File> createFile(@RequestBody final File file) {

        try {
            File _file = fileService.createFile(file);
            return new ResponseEntity<>(_file, HttpStatus.CREATED);
        } catch (Exception exception){
            exception.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<File> _file = fileService.findFileById(id);
        if (_file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This file doesn't exist.");
        }
        try {
            fileService.deleteFileById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
