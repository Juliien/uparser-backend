package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeService;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaServiceImpl;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import fr.esgi.grp9.uparserbackend.run.domain.RunServiceImpl;
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
    private final KafkaServiceImpl uParserProducerService;
    private final CodeService codeService;
    private final RunServiceImpl runService;


    public KafkaController(KafkaServiceImpl uParserProducerService, CodeService codeService, RunServiceImpl runService) {
        this.uParserProducerService = uParserProducerService;
        this.codeService = codeService;
        this.runService = runService;
    }

    @PostMapping("/produce/{id}")
    public ResponseEntity<Run> produce(@RequestBody final KafkaTransaction kafkaTransaction, @PathVariable String id) {
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

                    Code newCode = this.codeService.addCode(code);
                    runResult.setCodeId(newCode.getId());
                    this.runService.createRun(runResult);
                }
                return new ResponseEntity<>(runResult, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
