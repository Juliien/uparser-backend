package fr.esgi.grp9.uparserbackend.kafka.domain.customexception;

//Should be used when a wrong value is given for the kafka partition property
public class InvalidPartitionPropertyException extends Exception{

    public InvalidPartitionPropertyException(String invalidPartitionValue) {
        super("The value \"" +
                invalidPartitionValue
                + "\" is not a valid partition number argument");
    }

}