package fr.esgi.grp9.uparserbackend.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import fr.esgi.grp9.uparserbackend.run.domain.RunRaw;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public interface IKafkaService {
    Properties propertiesProvider(String action, String groupId);
    ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction);
    Producer<String, KafkaTransaction> createKafkaProducer(String groupId);
    KafkaConsumer<String, String> createKafkaConsumer(String groupId);
    RunRaw seekForRunnerResults(String runId) throws JsonProcessingException;
}
