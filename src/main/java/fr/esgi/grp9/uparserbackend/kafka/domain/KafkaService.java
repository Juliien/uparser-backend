package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public interface KafkaService {
    Properties propertiesProvider(String action, String groupId);
    ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction);
    Producer<String, KafkaTransaction> createKafkaProducer(String groupId);
    KafkaConsumer<String, KafkaTransaction> createKafkaConsumer(String groupId);
    ParserMetaData createParserMetaData(RecordMetadata recordMetadata);
    KafkaTransaction seekForRunnerResults(String runId);
}
