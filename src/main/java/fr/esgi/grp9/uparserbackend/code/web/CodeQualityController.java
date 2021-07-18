package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.quality.QualityServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/code")
public class CodeQualityController {

    private final QualityServiceImpl codeQualityService;

    public CodeQualityController(QualityServiceImpl codeQualityService) {
        this.codeQualityService = codeQualityService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Code>> getAllCodes(){
        return new ResponseEntity<>(this.codeQualityService.findAllCodes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Code> getCodeById(@PathVariable String id){
        return new ResponseEntity<>(this.codeQualityService.findById(id).get(), HttpStatus.OK);
    }

    @PostMapping("/quality")
    public ResponseEntity<Code> postCode(@RequestBody Code code) {
        try {
            Code codeVerified = this.codeQualityService.testCode(code);
            if(codeVerified != null)  {
                return new ResponseEntity<>(codeVerified , HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<Code>> getCodeHistory(@PathVariable String id) {
        if(id != null) {
            try {
                return new ResponseEntity<>( this.codeQualityService.getUserCodeHistory(id), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
