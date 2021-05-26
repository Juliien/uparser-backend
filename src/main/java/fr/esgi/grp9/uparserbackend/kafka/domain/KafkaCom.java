package fr.esgi.grp9.uparserbackend.kafka.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import fr.esgi.grp9.uparserbackend.kafka.domain.common.Message;
import fr.esgi.grp9.uparserbackend.kafka.domain.common.Tools;
import fr.esgi.grp9.uparserbackend.kafka.domain.customexception.*;

public class KafkaCom {

    private final Properties appProps;

    public KafkaCom(Properties appProps){
        this.appProps = appProps;
    }

    public Properties getAppProps() {
        return appProps;
    }

    public void iteratePartSet(Set<TopicPartition> partSet, KafkaConsumer<String, String> consumer) throws IOException {

        Map<TopicPartition, Long> beginningOffsets;
        Map<TopicPartition, Long> endOffsets;

        //Get the first offset for the given partitions inside the Set<TopicPartition> partSet
        beginningOffsets = consumer.beginningOffsets(partSet);
        //Get the end offsets for the given partitions inside the Set<TopicPartition> partSet
        endOffsets = consumer.endOffsets(partSet);

        System.out.println("Partitions state :");

        //Iterate for each partitions inside the Set<TopicPartition> partSet
        for (TopicPartition partition : partSet) {

            //Get the first offset for the current partition from the Map<TopicPartition, Long> beginningOffsets
            Long beginningOffset = beginningOffsets.get(partition);
            //Get the last offset for the current partition from the Map<TopicPartition, Long> endOffsets
            Long endOffset = endOffsets.get(partition);

            //Display useful message for user
            Message.partitionStateMessage(partition.toString(), consumer.position(partition), beginningOffset, endOffset);
            Message.messageChoiceConsumerOffset(beginningOffset, endOffset);

            //Start while loop to get user inputs
            boolean exit = false;
            while(!exit){

                Scanner scanner = new Scanner( System.in );
                System.out.print("> ");
                String line = scanner.next();

                if(line.equals("exit")){
                    //If the input equals to "exit" the consumer seek to the last offset the current partition
                    System.out.println("The offset won't be changed for the partition : " + partition.toString() + " .");
                    break;
                } else if (Tools.isNumeric(line)) {
                    //If the isNumeric function return true with the user input
                    if (Long.parseLong(line) <= endOffset && Long.parseLong(line) >= beginningOffset){
                        //If the input is between the beginningOffset value and the endOffset value
                        try {
                            //Overrides the fetch offsets that the consumer will use on the next poll and replace it with the user input
                            consumer.seek(partition, Long.parseLong(line));
                        }
                        catch (Exception ex)
                        {
                            System.out.print(ex.getMessage());
                            throw new IOException(ex.toString());
                        }
                        exit = true;
                    } else {
                        //Display messages if the user input isn't between the beginningOffset value and the endOffset value
                        System.out.println("The number typed is out of range.");
                        System.out.println("Try again or type exit.");
                    }
                } else {
                    //Display messages if the user input isn't between the beginningOffset value and the endOffset value or isn't "exit"
                    System.out.println("The value typed is neither \"exit\" nor a number.");
                    System.out.println("Try again or type exit.");
                }
            }
        }
    }

    public Properties createKafkaProperties(String brokerAddress, int brokerPort){
        //Create a Properties object used to create the KafkaConsumer object
        Properties kafkaProps = new Properties();

        kafkaProps.put("bootstrap.servers", brokerAddress + ":" + brokerPort);
        kafkaProps.put("group.id", "valle");
        kafkaProps.put("enable.auto.commit", "true");
        kafkaProps.put("auto.commit.interval.ms", "1000");
        kafkaProps.put("session.timeout.ms", "30000");
        kafkaProps.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return kafkaProps;
    }

    public void consume() throws IOException, InvalidPartitionPropertyException, InvalidStartOptionPropertyException, MissingConfigurationTagException, InvalidActionPropertyException {
        Tools.checkProperties(this.appProps);

        String topicName = this.appProps.getProperty("topicName");
        //Create a int[] to store partition number from the string argument "partitions"
        int[] partitionNumbers = Tools.extractNumber(this.appProps.getProperty("partitions"));
        String brokersAddress = this.appProps.getProperty("brokersAddress");
        int brokersPort = Integer.parseInt(this.appProps.getProperty("brokersPort"));
        String startOption = this.appProps.getProperty("startOption");
        String elasticAddress = this.appProps.getProperty("elasticAddress");
        int elasticPort = Integer.parseInt(this.appProps.getProperty("elasticPort"));
        String elasticIndex = this.appProps.getProperty("elasticIndex");
        String elasticUsername = this.appProps.getProperty("elasticUsername");
        String elasticPassword = this.appProps.getProperty("elasticPassword");

        Properties kafkaProps = createKafkaProperties(brokersAddress, brokersPort);

        Set<TopicPartition> partSet = new HashSet<>();
        //Iterate for each number in the partitionNumber tab
        for(int partitionNumber : partitionNumbers){
            //Fill a Set<TopicPartition> entry with the topicName and the current partition number
            partSet.add(new TopicPartition(topicName, partitionNumber));
        }

        //Create a KafkaConsumer object with kafkaProps
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        //Assign to the KafkaConsumer object every TopicPartition inside the Set<TopicPartition> partSet
        consumer.assign(partSet);

        //Initiate a switch for every startOption value possible : regular | from-beginning | manual
        switch(startOption) {
            case "from-beginning":
                System.out.println("From beginning mode choose for this consumer.");
                //Seek to the first offset for each of the given partitions inside the Set<TopicPartition> partSet
                consumer.seekToBeginning(partSet);
                break;
            case "regular":
                System.out.println("Regular mode choose for this consumer.");
                //Seek to the last offset for each of the given partitions inside the Set<TopicPartition> partSet
                consumer.seekToEnd(partSet);
                break;
            case "manual":
                System.out.println("Manual mode choose for this consumer.");
                iteratePartSet(partSet, consumer);
                break;
            default:
                //Display messages if the value for the argument startOption isn't regular | from-beginning | manual
//                Message.unrecognizedValueForStartOptionArgMessage();
                Message.consumeMessage();
//                consumer.subscribe(Collections.singletonList(topicName));
                throw new InvalidStartOptionPropertyException(startOption);
        }

        //print the topic name
        System.out.println("Subscribed to topic " + topicName);

//        ClientForKafka client = new ClientForKafka(elasticPort, elasticAddress, elasticUsername, elasticPassword);

        Duration pollTick = Duration.ofMillis(3000);

        Duration timeBeforeTimeOut = Duration.ofMillis(5000);

        while (true) {
            /* Fetch data for the topics or partitions specified using one of the subscribe/assign APIs until duration value,
            then retries to fetch */
            ConsumerRecords<String, String> records = consumer.poll(pollTick);
            //Iterate for each ConsumerRecord fetched
            for (ConsumerRecord<String, String> record : records){
                // print the offset,key and value for the consumer records.
                System.out.printf("KAFKA | CONSUME     topic = %s    Partition = %d     Offset = %d     Value = %s\n",
                        record.topic(), record.partition(), record.offset(), record.value());
                // send to an elastic index
//                client.pushKafkaMessageToElasticIndex(elasticIndex, (int) record.offset(), record.value());
            }
//            consumer.close(timeBeforeTimeOut);
        }
    }

    public void produce() throws IOException, InvalidPartitionPropertyException, MissingConfigurationTagException, InvalidActionPropertyException {
        Tools.checkProperties(this.appProps);

        String brokersAddress = this.appProps.getProperty("brokersAddress");
        int brokersPort = Integer.parseInt(this.appProps.getProperty("brokersPort"));

        //Create a int[] to store partition number from the string argument "partitions"
        int[] partitionNumbers = Tools.extractNumber(this.appProps.getProperty("partitions"));

        //Create a Properties object used to create the KafkaProducer object
        Properties props = new Properties();

        Properties kafkaProps = createKafkaProperties(brokersAddress, brokersPort);

        Set<TopicPartition> partSet = new HashSet<>();

        //Iterate for each number in the partitionNumber tab
        for(int partitionNumber : partitionNumbers){
            //Fill a Set<TopicPartition> entry with the topicName and the current partition number
            partSet.add(new TopicPartition(this.appProps.getProperty("topicName"), partitionNumber));
        }

        //Create a KafkaProducer object with props
        Producer<String, String> producer = new KafkaProducer<>(kafkaProps);


        //Create a PartitionInfo list which will contains every partition inside the topic aimed
        List<PartitionInfo> list = producer.partitionsFor(this.appProps.getProperty("topicName"));

        Message.messageEveryPartitionInTopic(this.appProps.getProperty("topicName"), list, partSet);

        Message.messageChoiceProducerInput();

        //Start while loop to get user inputs
        boolean sortie = false;
        while(!sortie){
            Scanner scanner = new Scanner( System.in );
            System.out.print("> ");

            String line = "";
            line += scanner.nextLine();

            if(line.equals("exit")){
                //If the input equals to "exit" the program stop
                sortie = true;
            } else {
                //If the input isn't equals to "exit" the program stop
                try {
                    //Iterate for each partition number inside the int[] partitionNumbers
                    for (int num: partitionNumbers) {
                        //Create a ProducerRecord object with the topic name, the current partition number, a key value equals to null and the user input
                        ProducerRecord record = new ProducerRecord<String, String>(
                                this.appProps.getProperty("topicName"),num, null,line
                        );
                        //Asynchronously send the ProducerRecord object record to the topic
                        producer.send(record).get();
                        System.out.println("Message sent successfully to partition "+ num + ".");
                    }
                }
                catch (Exception ex)
                {
                    System.out.print(ex.getMessage());
                    throw new IOException(ex.toString());
                }
            }
        }
        //Close the producer
        producer.close();
    }

}