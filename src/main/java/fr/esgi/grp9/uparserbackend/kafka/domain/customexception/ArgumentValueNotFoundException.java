package fr.esgi.grp9.uparserbackend.kafka.domain.customexception;

public class ArgumentValueNotFoundException extends Exception{
    public ArgumentValueNotFoundException(String argument){
        super("The Argument " + argument + " doesn't exist in the command.");
    }
}
