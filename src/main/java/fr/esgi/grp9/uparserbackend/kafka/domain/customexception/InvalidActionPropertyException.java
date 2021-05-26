package fr.esgi.grp9.uparserbackend.kafka.domain.customexception;

public class InvalidActionPropertyException extends Exception{

    public InvalidActionPropertyException(String wrongActionValue){
        super("The value " + wrongActionValue + " for the property \"action\" is not valid.");
    }
}
