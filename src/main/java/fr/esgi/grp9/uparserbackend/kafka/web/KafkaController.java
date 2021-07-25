package fr.esgi.grp9.uparserbackend.kafka.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeService;
import fr.esgi.grp9.uparserbackend.kafka.service.KafkaService;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import fr.esgi.grp9.uparserbackend.run.service.RunService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaService uParserProducerService;
    private final CodeService codeService;
    private final RunService runService;


    public KafkaController(KafkaService uParserProducerService, CodeService codeService, RunService runService) {
        this.uParserProducerService = uParserProducerService;
        this.codeService = codeService;
        this.runService = runService;
    }

    @PostMapping("/produce/{id}")
    public ResponseEntity<Run> produce(@RequestBody final KafkaTransaction kafkaTransaction, @PathVariable String id) {
        String transactionId = UUID.randomUUID().toString();
        kafkaTransaction.setId(transactionId);
        RunRaw runnerOutput;

        try {
            uParserProducerService.sendProducerRecord(kafkaTransaction);
            runnerOutput = uParserProducerService.seekForRunnerResults(kafkaTransaction.getId());
        } catch (ExecutionException | InterruptedException | TimeoutException | JsonProcessingException e) {
//            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Run runResult = Run.builder()
                .id(runnerOutput.getRun_id())
                .userId(id)
                .codeId(null)
                .stdout(runnerOutput.getStdout())
                .stderr(runnerOutput.getStderr())
                .artifact(runnerOutput.getArtifact())
                .stats(null)
                .creationDate(LocalDateTime.now())
                .build();

        if(!id.equals("1") && runnerOutput.getStderr().isEmpty()) {
            Code code = Code.builder()
                    .codeEncoded(kafkaTransaction.getAlgorithm())
                    .extensionStart(kafkaTransaction.getFrom())
                    .extensionEnd(kafkaTransaction.getTo())
                    .language(kafkaTransaction.getLanguage())
                    .date(new Date())
                    .build();
            try {
                runResult.setCodeId(this.codeService.addCode(code).getId());
                this.runService.createRun(runResult);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return new ResponseEntity<>(runResult, HttpStatus.OK);
    }
}
