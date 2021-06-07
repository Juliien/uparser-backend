package fr.esgi.grp9.uparserbackend.historic.web;

import fr.esgi.grp9.uparserbackend.historic.domain.Historic;
import fr.esgi.grp9.uparserbackend.historic.domain.HistoricService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historic")
public class HistoricController {

    private final HistoricService historicService;

    public HistoricController(HistoricService historicService) {
        this.historicService = historicService;
    }

    @PostMapping
    public ResponseEntity<Historic> insertCode(@RequestBody Historic historic) {
        try {
            Historic _historic = this.historicService.insertCode(historic);
            return new ResponseEntity<>(_historic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
