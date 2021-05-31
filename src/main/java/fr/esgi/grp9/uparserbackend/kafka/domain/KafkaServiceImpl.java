package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Value("${kafka.cluster.topic.name}")
    private String topicName;

    @Value("${kafka.cluster.address}")
    private String brokerAddress;

    @Value("${kafka.cluster.port}")
    private String brokerPort;

    @Override
    public Producer<String, KafkaTransaction> createKafkaProducer() {
        Properties props = new Properties();

        props.put("bootstrap.servers", brokerAddress + ":" + brokerPort);

//        props.put("request.timeout.ms", 5000);
//        props.put("linger.ms", 5000);
//        props.put("delivery.timeout.ms", 10000);
        props.put("max.block.ms", 3000);
//        props.put("socket.connection.setup.timeout.max.ms", 3000);
//        props.put("socket.connection.setup.timeout.ms", 3000);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.springframework.kafka.support.serializer.JsonSerializer");

        return  new KafkaProducer<String, KafkaTransaction>(props);
    }

    @Override
    public Consumer<String, KafkaTransaction> createKafkaConsumer() {
        Properties props = new Properties();

        props.put("bootstrap.servers", brokerAddress + ":" + brokerPort);
        props.put("request.timeout.ms", 5000);
        props.put("linger.ms", 100);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.springframework.kafka.support.serializer.JsonSerializer");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.deserializer",
                "org.springframework.kafka.support.serializer.JsonSerializer");

        return  new KafkaConsumer<String, KafkaTransaction>(props);
    }

    @Override
    public ParserMetaData createParserMetaData(RecordMetadata recordMetadata) {
        return new ParserMetaData(recordMetadata.topic(),
                recordMetadata.partition(),
                recordMetadata.offset(),
                recordMetadata.timestamp(),
                recordMetadata.serializedKeySize(),
                recordMetadata.serializedValueSize());
    }

    @Override
    public ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction) {
        return new ProducerRecord<String, KafkaTransaction>(this.topicName, kafkaTransaction.getIdRun(), kafkaTransaction);
    }
}
