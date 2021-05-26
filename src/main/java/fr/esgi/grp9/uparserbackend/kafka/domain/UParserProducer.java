package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.security.core.parameters.P;

import java.util.Properties;

public class UParserProducer {
//    final private Properties properties;
    final private Producer<String, String> kafkaProducer;

    public UParserProducer() {
//        this.properties = properties;

        //prendre les valeurs du fichier .properties
        Properties kafkaProperties = new Properties();
        String brokerAddress = null;

        int brokerPort = Integer.parseInt(null);
        kafkaProperties.put("bootstrap.servers", brokerAddress + ":" + brokerPort);
        kafkaProperties.put("group.id", "valle");
        kafkaProperties.put("enable.auto.commit", "true");
        kafkaProperties.put("auto.commit.interval.ms", "1000");
        kafkaProperties.put("session.timeout.ms", "30000");
        kafkaProperties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        this.kafkaProducer = new KafkaProducer<>(kafkaProperties);
    }
}
