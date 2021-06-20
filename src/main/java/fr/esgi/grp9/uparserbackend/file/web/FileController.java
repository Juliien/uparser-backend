package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
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
<<<<<<< HEAD
    public ResponseEntity<File> getFileById(@PathVariable String id){
=======
    public ResponseEntity<File> getFileByName(@PathVariable String id){

        File _fileExist = fileService.findFileById(id);

//        if(_fileExist == null){
//            return new ResponseEntity<>(HttpStatus.)
//        }

>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
        return new ResponseEntity<>(fileService.findFileById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<File> createFile(@RequestBody final File file) {

<<<<<<< HEAD
        try {
            File _file = fileService.createFile(file);
            return new ResponseEntity<>(_file, HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
=======
        File _fileExist = fileService.findFileById(file.getId());
        if (_fileExist == null){
            try {
                File _file = fileService.createFile(file);
                return new ResponseEntity<>(_file, HttpStatus.CREATED);
            } catch (Exception exception){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
    }

    @PutMapping
    public ResponseEntity<File> modifyFile(@RequestBody final File file){
<<<<<<< HEAD
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {

        File _fileExist = fileService.findFileById(id);

        if (_fileExist != null) {
            try {
                fileService.deleteFileById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception exception) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

=======

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
}
