package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public interface KafkaService {
    ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction);
    Producer<String, KafkaTransaction> createKafkaProducer();
    Consumer<String, KafkaTransaction> createKafkaConsumer();
    ParserMetaData createParserMetaData(RecordMetadata recordMetadata);
}
