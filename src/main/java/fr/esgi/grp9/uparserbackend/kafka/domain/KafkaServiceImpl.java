package fr.esgi.grp9.uparserbackend.kafka.domain;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Future;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Value("${kafka.cluster.topic.name}")
    private String topicName;

    @Value("${kafka.cluster.address}")
    private String brokerAddress;

    @Value("${kafka.cluster.port}")
    private String brokerPort;

    @Override
    public Producer<String, Transaction> createKafkaProducer() {
        Properties props = new Properties();

        props.put("bootstrap.servers", brokerAddress + ":" + brokerPort);
        props.put("request.timeout.ms", 5000);
        props.put("linger.ms", 100);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.springframework.kafka.support.serializer.JsonSerializer");

        return  new KafkaProducer<String, Transaction>(props);
    }

    @Override
    public ProducerRecord createProducerRecord(Transaction transaction) {
        String jsonMessage = "temp value";

        //TODO serialize the transaction content in a json

        return  new ProducerRecord<String, Transaction>(this.topicName, transaction.getId(), transaction);

//        Optional<Future<RecordMetadata>> future = Optional.of(producer.send(producerRecord,
//                (metadata, e) -> {
//                    if(e != null) {
//                        e.printStackTrace();
//                    } else {
//                        System.out.println("The offset of the record we just sent is: " + metadata.offset());
//                    }
//                }));
    }
}
