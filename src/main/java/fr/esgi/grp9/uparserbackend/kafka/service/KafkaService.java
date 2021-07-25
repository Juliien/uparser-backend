package fr.esgi.grp9.uparserbackend.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
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

    @Override
    public void sendProducerRecord(KafkaTransaction kafkaTransaction) throws ExecutionException, InterruptedException, TimeoutException {
        Properties props = propertiesProvider();
        KafkaProducer<String, KafkaTransaction> kafkaProducer = new KafkaProducer<String, KafkaTransaction>(props);

        ProducerRecord<String, KafkaTransaction> producerRecord = new ProducerRecord<String, KafkaTransaction>(
                this.topicNameProduce, kafkaTransaction.getId(), kafkaTransaction);

        Future<RecordMetadata> futureResult = kafkaProducer.send(producerRecord);
        try{
            futureResult.get(5000, TimeUnit.MILLISECONDS);
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
        } catch (KafkaException e) {
            kafkaProducer.abortTransaction();
        } finally {
            kafkaProducer.close();
        }
    }

    @Override
    public RunRaw seekForRunnerResults(String soughtRunId) throws JsonProcessingException {
        Properties props = propertiesProvider();
        props.setProperty("group.id", "group_" + soughtRunId);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
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
    public Properties propertiesProvider() {
        Properties appProps = new Properties();
        try {
            String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String appConfigPath = rootPath + "application-dev.properties";
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return appProps;
    }

}
