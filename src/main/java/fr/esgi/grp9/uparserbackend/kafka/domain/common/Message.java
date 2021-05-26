package fr.esgi.grp9.uparserbackend.kafka.domain.common;

import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Set;

//Create java class named “Message”
public class Message {

    //Display a help message that tells how to give parameters to the program
    public static void helpMessage() {
        System.out.println("Help Message :");
        System.out.println("List of the possible arguments with it description :");
        System.out.println("    - action = \n" +
                "       produce (launch the program with the mode kafka producer). \n" +
                "       consume (launch the program with the mode kafka consumer). \n" +
                "       help    (display this help message).");
        System.out.println("    - brokersAddress (The kafka broker address that is aimed by the kafka producer or consumer). \n" +
                "       example : (127.0.0.1; localhost)");
        System.out.println("    - brokersPort (The kafka broker port that is aimed by the kafka producer or consumer). \n" +
                "       example : (8000 ; 3000 ; 9092)");
        System.out.println("    - topicName (The name of the kafka topic that is aimed by the kafka producer or consumer). \n" +
                "       example : (kafkaTopic)");
        System.out.println("    - partitions (Partitions of the kafka topic that are aimed by the kafka producer or consumer). \n" +
                "       example : (0 ; 0,1 ; 0,2)");
        System.out.println("    - startOption (Start option when the consumer mode is selected) = \n" +
                "       regular (Start the kafka consumer at the last offset he previously reviewed.) \n" +
                "       from-beginning (Start the kafka consumer at the first offset of the partition.) \n" +
                "       manual (Allow the user to choose the starting point at each partitions");
        System.out.println("    - elasticAddress (The elastic broker address that is aimed by the kafka consumer). \n" +
                "       example : (127.0.0.1; localhost)");
        System.out.println("    - elasticPort (The elastic broker port that is aimed by the kafka consumer). \n" +
                "       example : (8000 ; 3000 ; 9092)");
        System.out.println("    - elasticIndex (The name of the elastic index that is aimed by the kafka consumer). \n" +
                "       example : (elastic-index)");
        System.out.println("    - elasticUsername (The username used for the login to the elastic index that is aimed by the kafka consumer). \n" +
                "       example : (elastic-username)");
        System.out.println("    - elasticPassword (The password used for the login to the elastic index that is aimed by the kafka consumer). \n" +
                "       example : (elastic-password)");
    }

    //Display a default message that display possible arguments value for the action tag
    public static void baseMessage() {
        System.out.println("Base Message :");
        System.out.println("   This program uses KAFKA and can generate a producer or/and a consumer. \n" +
                "   A file must be created in order to provide arguments. A template can be found under the name \"config.xml.example\" at the root level of the project.\n" +
                "   In the command you need to give the property file path with the arguments '--pathPropertyFile', example below.\n" +
                "       \" java -jar program.jar --pathPropertyFile ./config.xml \"\n" +
                "   The first possible argument that can be add in the property file is \"action\"\n" +
                "   It value can be = produce (launch a kafka producer) | consume (launch a kafka consumer) | help (display a help message)\n");
        helpMessage();
    }

    //Display a produce message that display possible arguments value when the action tag value equals produce
    public static void produceMessage() {
        System.out.println("Produce Message :");
        System.out.println("    Possible arguments :");
        System.out.println("    --brokersAddress : broker address (127.0.0.1, localhost)");
        System.out.println("    --brokersPort : broker port (8000, 3000, 9092)");
        System.out.println("    --topicName : \"the topic name\"");
        System.out.println("    --partitions : 0,1,2 for example");
    }

    //Display a consume message that display possible arguments value when the action tag value equals consume
    public static void consumeMessage() {
        System.out.println("Consume Message :");
        System.out.println("    Possible arguments :");
        System.out.println("    --brokersAddress : broker address (127.0.0.1, localhost)");
        System.out.println("    --brokersPort : broker port (8000, 3000, 9092)");
        System.out.println("    --topicName : \"the topic name\"");
        System.out.println("    --partitions : 0,1,2 for example");
        System.out.println("    --startOption : regular | from-beginning | manual");
        System.out.println("    --elasticAddress : elastic address (127.0.0.1, localhost, caas-search-ry-elastic-dev.elisa.saas.cagip.group.gca)");
        System.out.println("    --elasticPort : broker port (443, 3000, 9092)");
        System.out.println("    --elasticIndex : \"the index name\"");
        System.out.println("    --elasticUsername : the elastic username used for elastic login");
        System.out.println("    --elasticPassword : the elastic password used for elastic login");
    }

    public static void noArgMessage(String argName){
        System.out.println("No " + argName + " argument value found in the config.xml file.");
    }

    //Display a no partitions arg message that display the procedure to add the partitions parameter
    public static void noPartitionsArgMessage() {
        System.out.println("No partition found neither in the config.xml file nor in argument. " +
                "Try to use the --partitions argument followed by numbers inside brackets separated by comma like this [0,1,2] for example.");
    }

    //Display a no start option arg message that display the procedure to add the startOption parameter
    public static void noStartOptionArgMessage() {
        System.out.println("No start option found neither in the config.xml file nor in argument. " +
                "Try to use the --startOption argument followed by regular | from-beginning | manual.");
    }

    /*Display a unrecognized value for start option arg message
    that display to the user that an unrecognized value was use for the tag startOption*/
    public static void unrecognizedValueForStartOptionArgMessage() {
        System.out.println("Unrecognized value for --startOption argument, default startOption used.");
    }

    //Display a no topic name arg message that display the procedure to add the topicName parameter
    public static void noTopicNameArgMessage() {
        System.out.println("No topic name found neither in the config.xml file nor in argument. " +
                "Try to use the --topicName argument followed by the topic name.");
    }

    //Display a no brokers arg message that display the procedure to add the brokers parameter
    public static void noBrokersArgMessage() {
        System.out.println("No brokers address and port found neither in the config.xml file nor in argument. " +
                "Try to use the --brokers argument followed by the brokers address and port.");
    }

    //Display a no action arg message that display the procedure to add the action parameter
    public static void noActionArgMessage() {
        System.out.println("No action found neither in the config.xml file nor in argument. " +
                "Try to use the --action argument followed by produce | consume | help");
    }

    //Display a partition state message that display information about the state of the partition
    public static void partitionStateMessage(String partition, Long position, Long beginningOffset, Long endOffset) {
        //Display information about the state of the current partition
        System.out.println("    partition : " + partition);
        System.out.println("    The consumer offset for this partition : " + position);
        System.out.println("    This partition starts at the offset : " + beginningOffset);
        System.out.println("    This partition ends at the offset : " + endOffset + "\n");
    }

    //Display a message choice consumer offset
    public static void messageChoiceConsumerOffset(Long beginningOffset, Long endOffset) {
        //Display choice options for consumer offset
        System.out.println("You can now choose the consumer offset.");
        System.out.println("Your options are :");
        System.out.println("    - Type \"a number\" between " + beginningOffset + " and " + endOffset + "" +
                " to set the offset.");
        System.out.println("    - Type \"exit\" to quit.");
    }

    //Display a message every partition in topic
    public static void messageEveryPartitionInTopic(String topicName, List<PartitionInfo> list, Set<TopicPartition> partSet) {
        //Display each partition inside the topic aimed
        System.out.println("Your messages will be sent to the topic : " + topicName);
        for (PartitionInfo info : list) {
            for (TopicPartition partition : partSet) {
                if(partition.partition() == info.partition()) {
                    System.out.println("At this partition : " + partition.toString());
                }
            }
        }
    }

    //Display a message choice producer input
    public static void messageChoiceProducerInput() {
        //Display choice options for producer input
        System.out.println("You can now type your message.");
        System.out.println("Your options are :");
        System.out.println("    - Type \"a string\".");
        System.out.println("    - Type \"exit\" to quit.");
    }

    //Display a message with the config.xml.example
    public static void messageConfigExample(){
        System.out.println("You can see below the content of the config.xml.example.\n");
        System.out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n" +
                "<properties>\n" +
                "    <entry key=\"action\"></entry>\n" +
                "    <entry key=\"brokersAddress\"></entry>\n" +
                "    <entry key=\"brokersPort\"></entry>\n" +
                "    <entry key=\"topicName\"></entry>\n" +
                "    <entry key=\"partitions\"></entry>\n" +
                "    <entry key=\"startOption\"></entry>\n" +
                "    <entry key=\"elasticAddress\"></entry>\n" +
                "    <entry key=\"elasticPort\"></entry>\n" +
                "    <entry key=\"elasticIndex\"></entry>\n" +
                "    <entry key=\"elasticUsername\"></entry>\n" +
                "    <entry key=\"elasticPassword\"></entry>\n" +
                "</properties>");
    }

}