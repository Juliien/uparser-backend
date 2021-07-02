package fr.esgi.grp9.uparserbackend.exception.file;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class FileNameEmptyException extends RuntimeException{
    public FileNameEmptyException(String message) {
        super(message);
    }
}
