package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Future;

public interface KafkaService {
    ProducerRecord createProducerRecord(Transaction transaction);
    Producer<String, Transaction> createKafkaProducer();
}
