package fr.esgi.grp9.uparserbackend.kafka.domain;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class KafkaServiceImpl implements KafkaService{

    @Value("${kafka.cluster.topic.produce.name}")
    private String topicNameProduce;

    @Value("${kafka.cluster.topic.consume.name}")
    private String topicNameConsume;

    @Value("${kafka.cluster.address}")
    private String brokerAddress;

    @Value("${kafka.cluster.port}")
    private String brokerPort;

    @Override
    public Producer<String, KafkaTransaction> createKafkaProducer(String groupId) {
        Properties props = propertiesProvider("produce", groupId);
        return  new KafkaProducer<String, KafkaTransaction>(props);
    }

    @Override
    public KafkaConsumer<String, KafkaTransaction> createKafkaConsumer(String groupId) {
        Properties properties = propertiesProvider("consume", groupId);
        return  new KafkaConsumer<String, KafkaTransaction>(properties);
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
    public KafkaTransaction seekForRunnerResults(String soughtRunId) {
        KafkaConsumer<String, KafkaTransaction> consumer = createKafkaConsumer(soughtRunId);

        consumer.subscribe(Collections.singletonList(topicNameProduce));

//        Set<TopicPartition> partSet = new HashSet<>();
//        TopicPartition topicPartition = new TopicPartition(topicNameConsume, 0);
//        partSet.add(topicPartition);
//        consumer.assign(partSet);
//        consumer.seekToBeginning(partSet);

        boolean keep = true;
        KafkaTransaction kafkaTransaction = null;

        while (keep) {
            ConsumerRecords<String, KafkaTransaction> consumerRecords =
                    consumer.poll(Duration.ofMillis(3000));

            for (ConsumerRecord<String, KafkaTransaction> record : consumerRecords) {
                System.out.printf("KAFKA | CONSUME     topic = %s    Partition = %d     Offset = %d     Value = %s\n",
                        record.topic(), record.partition(), record.offset(), record.value());
                kafkaTransaction = record.value();
                if(kafkaTransaction.getRunId() != null){
                    if(kafkaTransaction.getRunId().equals(soughtRunId)){
                        keep = false;
                        consumer.commitSync();
                        consumer.close();
                        break;
                    }
                }
            }
        }
        return kafkaTransaction;
    }

    @Override
    public Properties propertiesProvider(String action, String groupId) {
        Properties props = new Properties();

        switch (action){
            case "consume":
                props.put("bootstrap.servers", brokerAddress + ":" + brokerPort);
                props.put("request.timeout.ms", 5000);
                props.put("group.id", "groupForRunId = " + groupId);
                props.put("enable.auto.commit", "false");
                props.put("key.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer",
                        "fr.esgi.grp9.uparserbackend.kafka.domain.serde.TransactionDeserializer");
                break;
            case "produce":
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
                        "fr.esgi.grp9.uparserbackend.kafka.domain.serde.TransactionSerializer");
        }

        return props;
    }

    @Override
    public ProducerRecord<String, KafkaTransaction> createProducerRecord(KafkaTransaction kafkaTransaction) {
        return new ProducerRecord<String, KafkaTransaction>(this.topicNameProduce, kafkaTransaction.getUserId(), kafkaTransaction);
    }

}
