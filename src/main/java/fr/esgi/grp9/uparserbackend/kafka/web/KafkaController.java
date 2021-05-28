package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.kafka.domain.Transaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaServiceImpl;
import org.apache.kafka.clients.producer.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/kafka/")
public class KafkaController {
    private final KafkaServiceImpl uParserProducerService;

    public KafkaController(KafkaServiceImpl uParserProducerService) {
        this.uParserProducerService = uParserProducerService;
    }

    @PostMapping("/produce")
    public ResponseEntity<RecordMetadata> produce(@RequestBody final Transaction transaction) {

        //TODO get les files par id
        String _fileExist = "";

        //TODO checker si ils existent et sinon renvoyer un bad request
        if(_fileExist == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Producer<String, Transaction> producer = uParserProducerService.createKafkaProducer();

            ProducerRecord producerRecord = uParserProducerService.createProducerRecord(transaction);

            try{
                producer.send(producerRecord,
                        new Callback() {
                            public void onCompletion(RecordMetadata metadata, Exception e) {
                                if(e != null) {
                                    e.printStackTrace();
                                } else {
                                    System.out.println("The offset of the record we just sent is: " + metadata.offset());
                                }
                        }}).get();
            } catch (Exception exception){
                System.out.println(exception.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

//            Optional<Future<RecordMetadata>> future = uParserProducerService.produce(transaction);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
