package fr.esgi.grp9.uparserbackend.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.kafka.domain.ParserMetaData;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaService implements IKafkaService {

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

        return ParserMetaData.builder()
                .topic(result.topic())
                .partition(result.partition())
                .baseOffset(result.offset())
                .timestamp(result.timestamp())
                .serializedKeySize(result.serializedKeySize())
                .serializedValueSize(result.serializedValueSize())
                .build();
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
                run = new ObjectMapper().readValue(record.value(), RunRaw.class);
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
