package fr.esgi.grp9.uparserbackend.kafka.web;

import fr.esgi.grp9.uparserbackend.kafka.domain.Extension;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaServiceImpl;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/kafka/")
public class KafkaController {
    private final KafkaServiceImpl uParserProducerService;

    public KafkaController(KafkaServiceImpl uParserProducerService) {
        this.uParserProducerService = uParserProducerService;
    }

        @PostMapping("/produce50")
    public ResponseEntity<KafkaTransaction> newProduce(@RequestBody final KafkaTransaction kafkaTransaction) {
        System.out.println("sending 50");
        Producer<String, KafkaTransaction> producer = uParserProducerService.createKafkaProducer(kafkaTransaction.getRunId());

        for (int i = 0; i < 100; i++) {
            KafkaTransaction kafkaTransaction1 = new KafkaTransaction(String.valueOf(i),
                    kafkaTransaction.getUserId(),
                    kafkaTransaction.getFileName(),
                    kafkaTransaction.getFileContent(),
                    kafkaTransaction.getCode(),
                    kafkaTransaction.getExtensionEnd());

            ProducerRecord<String, KafkaTransaction> producerRecord = uParserProducerService.createProducerRecord(kafkaTransaction1);
            Future<RecordMetadata> futureResult = producer.send(producerRecord);

            try {
                RecordMetadata result = futureResult.get(5000, TimeUnit.MILLISECONDS);

            } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                // We can't recover from these exceptions, so our only option is to close the producer and exit.
                System.out.println("1" + e.getMessage());
                producer.close();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (KafkaException e) {
                System.out.println("2" + e.getMessage());
                // For all other exceptions, just abort the transaction and try again.
                producer.abortTransaction();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (ExecutionException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (InterruptedException e) {
                System.out.println("4" + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (TimeoutException e) {
                System.out.println("5" + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        producer.close();
        return new ResponseEntity<>(kafkaTransaction, HttpStatus.OK);
    }

    @PostMapping("/produceBlank")
    public ResponseEntity<KafkaTransaction> produceBlank(@RequestBody final KafkaTransaction kafkaTransaction) {

        KafkaTransaction runnerResult = uParserProducerService.seekForRunnerResults("123");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/produce")
    public ResponseEntity<KafkaTransaction> produce(@RequestBody final KafkaTransaction kafkaTransaction) {
        System.out.println("sending " + kafkaTransaction.getRunId());

        //TODO get les files par id
        String _fileExist = "";

        //TODO checker si ils existent et sinon renvoyer une bad request
        if(_fileExist == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Producer<String, KafkaTransaction> producer = uParserProducerService.createKafkaProducer(kafkaTransaction.getRunId());

                ProducerRecord<String, KafkaTransaction> producerRecord = uParserProducerService.createProducerRecord(kafkaTransaction);
//                Future<RecordMetadata> futureResult = producer.send(producerRecord);

                try {

                    //lancer un consumer qui consume jusqu'a trouver l'id de run qui correspond

                    //renvoyer dans la response

                    //ici chercher dans la queue le message de retour et le renvoyer dans la response

                    //voir ce qu'il faut garder en base ici

                    //si fichier valide on garde le resultat dans la base

                    //faire matcher les id du fichier et des resultats du run

//                    RecordMetadata result = futureResult.get(5000, TimeUnit.MILLISECONDS);
//                    ParserMetaData resultParserMetaData = uParserProducerService.createParserMetaData(result);

                    try{
                        KafkaTransaction runnerResult = uParserProducerService.seekForRunnerResults(kafkaTransaction.getRunId());
                        return new ResponseEntity<>(runnerResult, HttpStatus.OK);
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

//                    return new ResponseEntity<>(resultParserMetaData, HttpStatus.OK);


                } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                    // We can't recover from these exceptions, so our only option is to close the producer and exit.
                    System.out.println("1" + e.getMessage());
                    producer.close();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (KafkaException e) {
                    System.out.println("2" + e.getMessage());
                    // For all other exceptions, just abort the transaction and try again.
                    producer.abortTransaction();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//                } catch (ExecutionException e) {
//                    System.out.println(e.getMessage());
//                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//                } catch (InterruptedException e) {
//                    System.out.println("4" + e.getMessage());
//                    e.printStackTrace();
//                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//                } catch (TimeoutException e) {
//                    System.out.println("5" + e.getMessage());
//                e.printStackTrace();
//                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    System.out.println("close");
                    producer.close();
                }


        }
    }
}
