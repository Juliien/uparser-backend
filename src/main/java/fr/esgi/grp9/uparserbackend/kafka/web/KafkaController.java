package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaServiceImpl;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<KafkaTransaction> produce(@RequestBody final KafkaTransaction kafkaTransaction) {

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
                    KafkaTransaction runnerResult = uParserProducerService.seekForRunnerResults(kafkaTransaction.getId());

                    //TODO faire l'envoi dans le bdd ici en fonction des résultats du run

                    return new ResponseEntity<>(runnerResult, HttpStatus.OK);
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
