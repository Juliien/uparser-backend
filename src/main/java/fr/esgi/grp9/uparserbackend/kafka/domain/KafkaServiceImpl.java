package fr.esgi.grp9.uparserbackend.kafka.domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaServiceImpl implements KafkaService{

    @Value("${kafka.cluster.topic.produce.name}")
    private String topicNameProduce;

    @Value("${kafka.cluster.topic.consume.name}")
    private String topicNameConsume;

    @Value("${kafka.cluster.address}")
    private String brokerAddress;

    @Value("${kafka.cluster.port}")
    private String brokerPort;

    @Override
    public Producer<String, KafkaTransaction> createKafkaProducer(String groupId) {
        Properties props = propertiesProvider("produce", groupId);
        return  new KafkaProducer<String, KafkaTransaction>(props);
    }

    @Override
    public ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction) {
        return new ProducerRecord<String, KafkaTransaction>(this.topicNameProduce, kafkaTransaction.getId(), kafkaTransaction);
    }

    @Override
    public KafkaConsumer<String, String> createKafkaConsumer(String groupId) {
        Properties properties = propertiesProvider("consume", groupId);
        return  new KafkaConsumer<String, String>(properties);
    }

    public ParserMetaData sendProducerRecord(KafkaTransaction kafkaTransaction) throws ExecutionException, InterruptedException, TimeoutException {
        RecordMetadata result = null;

        Properties props = propertiesProvider("produce", kafkaTransaction.getId());
        KafkaProducer<String, KafkaTransaction> kafkaProducer = new KafkaProducer<String, KafkaTransaction>(props);

        ProducerRecord<String, KafkaTransaction> producerRecord = new ProducerRecord<String, KafkaTransaction>(
                this.topicNameProduce, kafkaTransaction.getId(), kafkaTransaction);

        Future<RecordMetadata> futureResult = kafkaProducer.send(producerRecord);
        try{
            result = futureResult.get(5000, TimeUnit.MILLISECONDS);
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            System.out.println("1 " + e.getMessage());
        } catch (KafkaException e) {
            System.out.println("2 " + e.getMessage());
            // For all other exceptions, just abort the transaction and try again.
            kafkaProducer.abortTransaction();
        } finally {
            kafkaProducer.close();
        }

        return new ParserMetaData(result.topic(),
                result.partition(),
                result.offset(),
                result.timestamp(),
                result.serializedKeySize(),
                result.serializedValueSize());
    }

    @Override
    public RunRaw seekForRunnerResults(String soughtRunId) throws JsonProcessingException {
        KafkaConsumer<String, String> consumer = createKafkaConsumer(soughtRunId);
        consumer.subscribe(Collections.singletonList(topicNameConsume));

        boolean keep = true;
        RunRaw run = null;

        while (keep) {
            ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(Duration.ofMillis(3000));

            for (ConsumerRecord<String, String> record : consumerRecords) {

                String recordString = record.value();

                if (String.valueOf(record.value().charAt(0)).equals("\"") && String.valueOf(record.value().charAt(recordString.length()-1)).equals("\""))
                    recordString = record.value().substring(1, record.value().length() - 1);

                recordString = recordString.replace("'stdout': b", "'stdout': ");
                recordString = recordString.replace("'stderr': b", "'stderr': ");
                recordString = recordString.replace("\'", "\"");

                run = new ObjectMapper().readValue(recordString, RunRaw.class);
                if(run.getRun_id() != null){
                    if(run.getRun_id().equals(soughtRunId)){
                        keep = false;
                        consumer.commitSync();
                        consumer.close();
                        break;
                    }
                }
            }
        }
        return run;
    }

    @Override
    public Properties propertiesProvider(String action, String groupId) {
        Properties props = new Properties();

        //TODO implement timeout behaviour of consumer and producer
        // properties utiles pour configurer le cas de figure où le cluster KAFKA ne fonctionne pas
//        props.put("socket.connection.setup.timeout.max.ms", 3000);
//        props.put("socket.connection.setup.timeout.ms", 3000);
//        props.put("request.timeout.ms", 5000);
//        props.put("linger.ms", 5000);
//        props.put("delivery.timeout.ms", 10000);

        props.put("bootstrap.servers", brokerAddress + ":" + brokerPort);

        switch (action){
            case "consume":
                props.put("request.timeout.ms", 5000);
                props.put("group.id", "groupForRunId = " + groupId);
                props.put("enable.auto.commit", "false");
                props.put("key.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer");
                break;
            case "produce":
                props.put("max.block.ms", 3000);
                props.put("key.serializer",
                        "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer",
                        "fr.esgi.grp9.uparserbackend.kafka.domain.serde.TransactionSerializer");
        }

        return props;
    }

}
