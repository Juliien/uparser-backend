package fr.esgi.grp9.uparserbackend.kafka.domain.customexception;

public class MissingConfigurationTagException extends Exception{

    public MissingConfigurationTagException(String configTag){
        super("The value for the tag " + configTag + " is missing in the config.xml file.");
    }
}
