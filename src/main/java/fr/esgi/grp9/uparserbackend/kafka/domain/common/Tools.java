package fr.esgi.grp9.uparserbackend.kafka.domain.common;

import fr.esgi.grp9.uparserbackend.kafka.domain.customexception.InvalidActionPropertyException;
import fr.esgi.grp9.uparserbackend.kafka.domain.customexception.InvalidPartitionPropertyException;
import fr.esgi.grp9.uparserbackend.kafka.domain.customexception.MissingConfigurationTagException;
import fr.esgi.grp9.uparserbackend.kafka.domain.customexception.ArgumentValueNotFoundException;

import java.util.*;

//Create java class named “Tools”, this class contains usefull function that can be used in every other class
public class Tools {

    //This constant is an Array that list all possible property tag for this application
    static final ArrayList<String> ALL_PROPERTY_TAGS = new ArrayList<>(
            Arrays.asList("action",
                    "brokersAddress",
                    "brokersPort",
                    "topicName",
                    "partitions",
                    "startOption",
                    "elasticAddress",
                    "elasticPort",
                    "elasticIndex",
                    "elasticUsername",
                    "elasticPassword")
    );

    //This constant is an Array that list all possible property tag for this application in consume mode
    static final ArrayList<String> CONSUME_PROPERTY_TAGS = new ArrayList<>(
            Arrays.asList("action",
                    "brokersAddress",
                    "brokersPort",
                    "topicName",
                    "partitions",
                    "startOption",
                    "elasticAddress",
                    "elasticPort",
                    "elasticIndex",
                    "elasticUsername",
                    "elasticPassword")
    );

    //This constant is an Array that list all possible property tag for this application in produce mode
    static final ArrayList<String> PRODUCE_PROPERTY_TAGS = new ArrayList<>(
            Arrays.asList("action",
                    "brokersAddress",
                    "brokersPort",
                    "topicName",
                    "partitions",
                    "startOption")
    );

    //This function returns true if the String parameter can be parse into an int, false if not
    public static boolean isNumeric(String line){
        try {
            // checking valid integer using parseInt() method
            Integer.parseInt(line);
            return true;
        }
        catch (NumberFormatException e)
        {
            System.out.println(line + " is not a valid integer number");
            return false;
        }
    }

    //This function returns the value of the argument tag given in parameter
    public static String getArgumentValueByTag(String[] args, String argTag) throws Exception {
        String keyTag = "--" + argTag;
        try{
            return args[Arrays.asList(args).indexOf(keyTag)+1];
        }catch (ArrayIndexOutOfBoundsException e){
            throw new ArgumentValueNotFoundException(argTag);
        }
    }

    //This function returns an int[] based on a String given in parameter
    public static int[] extractNumber(String str) throws InvalidPartitionPropertyException, MissingConfigurationTagException {
        if(str.equals("")){
            throw new MissingConfigurationTagException(str);
        }

        String[] stringTab;
        stringTab = str.toLowerCase().split(",");
        int[] tab = new int[stringTab.length];
        try {
            int i = 0;
            for (String value: stringTab) {
                tab[i] = Integer.parseInt(value);
                i++;
            }
        } catch (NumberFormatException e) {
            if(str.equals("")){
                str = "Empty string";
            }
            throw new InvalidPartitionPropertyException(str);
        }
        return tab;
    }

    //This function return true if the String is consume | produce | help, false other wise
    public static boolean checkActionProperty(String actionValue){
        ArrayList<String> possibleActionValues = new ArrayList<>();
        possibleActionValues.add("consume");
        possibleActionValues.add("produce");
        possibleActionValues.add("help");
        return possibleActionValues.contains(actionValue);
    }

    //This function looks if there is a missing property and if a property has an empty string value
    public static void checkProperties(Properties properties) throws MissingConfigurationTagException, InvalidActionPropertyException {
        if(!checkActionProperty(properties.getProperty("action"))){
            throw new InvalidActionPropertyException(properties.getProperty("action"));
        }

        if(properties.getProperty("action").equals("consume")){
            for (String keyTag : CONSUME_PROPERTY_TAGS) {
                if(properties.getProperty(keyTag).equals("")){
                    Message.noArgMessage(keyTag);
                    throw new MissingConfigurationTagException(keyTag);
                }
            }
        }

        if(properties.getProperty("action").equals("produce")){
            for (String keyTag : PRODUCE_PROPERTY_TAGS) {
                if(properties.getProperty(keyTag).equals("")){
                    Message.noArgMessage(keyTag);
                    throw new MissingConfigurationTagException(keyTag);
                }
            }
        }
    }
}