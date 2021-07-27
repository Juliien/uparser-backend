package fr.esgi.grp9.uparserbackend.code.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.service.quality.QualityService;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/quality")
public class CodeQualityController {

    private final QualityService codeQualityService;

    public CodeQualityController(QualityService codeQualityService) {
        this.codeQualityService = codeQualityService;
    }


    @PostMapping
    public ResponseEntity<Code> postCode(@RequestBody Code code) {
        try {
            Code codeVerified = this.codeQualityService.testCodeQuality(code);
            if(codeVerified != null)  {
                return new ResponseEntity<>(codeVerified , HttpStatus.OK);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This code already exist.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/plagiarism")
    public ResponseEntity<Code> getCodeToPlagiarism(@RequestBody Code code) {
        try {
            return new ResponseEntity<>(this.codeQualityService.isCodePlagiarism(code), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/parse")
    public ResponseEntity<String> getCodeParsed(@RequestBody KafkaTransaction kafkaTransaction) {
        try {
            return new ResponseEntity<>(this.codeQualityService.parseFile(kafkaTransaction), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /*@GetMapping("/test/prepare")
    public ResponseEntity<String> getpreparedCode(){
        try {
            return new ResponseEntity<>(this.codeQualityService.prepareCode("def compteur_complet(start, stop, step):\n" +
                    "    i = start\n" +
                    "    if a < 12 \n" +
                   // "    while i < stop:\n" +
                 //   "         print(\"Ce perroquet ne pourra pas\", action)\n" +
                    "        i = i + step"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
}
