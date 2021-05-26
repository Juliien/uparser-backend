package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaCom;
import fr.esgi.grp9.uparserbackend.kafka.domain.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka/")
public class KafkaController {
    private final KafkaCom kafkaCom;

    public KafkaController(KafkaCom kafkaCom) {
        this.kafkaCom = kafkaCom;
    }

    @PostMapping("/produce")
    public ResponseEntity<String> produce(@RequestBody final Transaction transaction){
        //faire appel au producer ici
    }
}
