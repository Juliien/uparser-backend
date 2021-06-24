package fr.esgi.grp9.uparserbackend.mail.domain;

import lombok.Data;

@Data
public class Email {
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailSubject;
}
