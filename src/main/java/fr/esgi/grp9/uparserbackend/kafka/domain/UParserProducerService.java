package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.producer.ProducerRecord;

public interface UParserProducerService {
    UParserProducer createProducer(UParserProducer uParserProducer);
    ProducerRecord createProducerRecord(ProducerRecord producerRecord);
}
