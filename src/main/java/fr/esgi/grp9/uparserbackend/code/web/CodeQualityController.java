package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.service.quality.QualityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/quality")
public class CodeQualityController {

    private final QualityService codeQualityService;

    public CodeQualityController(QualityService codeQualityService) {
        this.codeQualityService = codeQualityService;
    }

//    //TODO revoir ça, changer le testcode qui return du code
//    @PostMapping
//    public ResponseEntity<Code> postCode(@RequestBody Code code) {
//        try {
//            Code codeVerified = this.codeQualityService.testCode(code);
//            if(codeVerified != null)  {
//                return new ResponseEntity<>(codeVerified , HttpStatus.OK);
//            }
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "This code already exist.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/plagiarism")
    public ResponseEntity<Code> getCodeToPlagiarism(@RequestBody Code code) {
        try {
            return new ResponseEntity<>(this.codeQualityService.isCodePlagiarism(code), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
