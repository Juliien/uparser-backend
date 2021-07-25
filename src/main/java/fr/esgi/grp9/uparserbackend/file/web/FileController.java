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
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<List<File>> _files;
        try {
            _files = this.fileService.getFilesByUserId(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (_files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't exist.");
        }
        return new ResponseEntity<>(_files.get(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id){
        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty id.");
        }
        Optional<File> _file;
        try {
            _file = this.fileService.findFileById(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (_file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This file doesn't exist.");
        }
        return new ResponseEntity<>(_file.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<File> createFile(@RequestBody final File file) {
        try {
            return new ResponseEntity<>(this.fileService.createFile(file), HttpStatus.CREATED);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        getFileById(id);
        try {
            this.fileService.deleteFileById(id);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}