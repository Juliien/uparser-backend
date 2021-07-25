package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/code")
public class CodeController {

    private final CodeService codeService;

    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<Code>> getCodeHistory(@PathVariable String id) {
        if(id != null) {
            try {
                return new ResponseEntity<>( this.codeService.getUserCodeHistory(id), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<Code> postCode(@RequestBody Code code) {
        try {
            return new ResponseEntity<>( this.codeService.addCode(code), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Code> enableCodeToCatalog(@RequestBody Code code) {
        return new ResponseEntity<>(this.codeService.enableCodeToCatalog(code), HttpStatus.OK);
    }
}
