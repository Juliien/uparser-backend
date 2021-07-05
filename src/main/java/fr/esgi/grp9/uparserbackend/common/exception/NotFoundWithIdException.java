package fr.esgi.grp9.uparserbackend.common.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NotFoundWithIdException extends RuntimeException{
    private final Map<String, Object> errors;

    public NotFoundWithIdException(String context, String id) {
        this.errors = new HashMap<>();
        this.errors.put("resource", context);
        this.errors.put("id", id);
        log.error("{} with id {} not found", context, id);
    }

    public Map<String, Object> getErrors(){
        return errors;
    }


}