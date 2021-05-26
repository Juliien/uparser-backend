package fr.esgi.grp9.uparserbackend.kafka.domain.customexception;

public class InvalidStartOptionPropertyException extends Exception{

    public InvalidStartOptionPropertyException(String invalidStartOptionValue){
        super("Unrecognized value for --startOption argument, default startOption used.\n" +
                "The value \"" +
                invalidStartOptionValue
                + "\" is not a valid startOption value argument.\n" +
                "Possible values are : regular | from-beginning | manual");
    }
}
