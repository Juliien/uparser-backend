package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeService;
import fr.esgi.grp9.uparserbackend.kafka.service.KafkaService;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import fr.esgi.grp9.uparserbackend.run.service.RunService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaService uParserProducerService;

    public KafkaController(KafkaService uParserProducerService) {
        this.uParserProducerService = uParserProducerService;
    }

    @PostMapping("/produce/{id}")
    public ResponseEntity<RunRaw> produce(@RequestBody final KafkaTransaction kafkaTransaction, @PathVariable String id) {
        String transactionId = UUID.randomUUID().toString();
        kafkaTransaction.setId(transactionId);
        ParserMetaData parserMetaData;

        try {
            parserMetaData = uParserProducerService.sendProducerRecord(kafkaTransaction);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (parserMetaData != null) {
            try {
                RunRaw runnerOutput = uParserProducerService.seekForRunnerResults(kafkaTransaction.getId());
                return new ResponseEntity<>(runnerOutput, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
