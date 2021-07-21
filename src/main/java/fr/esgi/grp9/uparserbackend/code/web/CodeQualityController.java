package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.quality.QualityServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quality")
public class CodeQualityController {

    private final QualityServiceImpl codeQualityService;

    public CodeQualityController(QualityServiceImpl codeQualityService) {
        this.codeQualityService = codeQualityService;
    }

    @PostMapping
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
}
