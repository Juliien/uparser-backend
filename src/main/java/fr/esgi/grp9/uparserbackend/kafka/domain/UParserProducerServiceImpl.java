package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.producer.ProducerRecord;

public class UParserProducerServiceImpl implements UParserProducerService{
    @Override
    public UParserProducer createProducer(UParserProducer uParserProducer) {
        return null;
    }

    @Override
    public ProducerRecord createProducerRecord(ProducerRecord producerRecord) {
        return new ProducerRecord<String, String>(
                this.appProps.getProperty("topicName"),num, null,line
        );
    }
}
