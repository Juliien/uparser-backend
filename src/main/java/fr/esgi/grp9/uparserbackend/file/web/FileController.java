package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileServiceImpl fileService;

    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<File>> getFiles() {
        return new ResponseEntity<>(this.fileService.getFiles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable String id){
        try {
            return new ResponseEntity<>(fileService.findFileById(id), HttpStatus.OK);
        } catch (NotFoundWithIdException notFoundWithIdException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<File> createFile(@RequestBody final File file) {

        try {
            File _file = fileService.createFile(file);
            return new ResponseEntity<>(_file, HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<File> updateFile(@RequestBody final File file){
        try{
            File _file = fileService.updateFile(file);
            return new ResponseEntity<>(_file, HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {

        File _fileExist = fileService.findFileById(id);

        if (_fileExist != null) {
            try {
                fileService.deleteFileById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception exception) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
