package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaServiceImpl;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaServiceImpl uParserProducerService;

    public KafkaController(KafkaServiceImpl uParserProducerService) {
        this.uParserProducerService = uParserProducerService;
    }

    @PostMapping("/produce")
    public ResponseEntity<Run> produce(@RequestBody final KafkaTransaction kafkaTransaction) {
        String transactionId = UUID.randomUUID().toString();
        kafkaTransaction.setId(transactionId);

        //TODO get les files par id
        String _fileExist = "";
        ParserMetaData parserMetaData;

        //TODO checker si ils existent et sinon renvoyer une bad request
        if(_fileExist == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
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
                            .userEmail(null)
                            .codeId(null)
                            .stdout(runnerOutput.getStdout())
                            .stderr(runnerOutput.getStderr())
                            .artifact(runnerOutput.getArtifact())
                            .stats(null)
                            .creationDate(LocalDateTime.now())
                            .build();

                    //TODO faire l'envoi dans le bdd ici en fonction des résultats du run

                    return new ResponseEntity<>(runResult, HttpStatus.OK);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
