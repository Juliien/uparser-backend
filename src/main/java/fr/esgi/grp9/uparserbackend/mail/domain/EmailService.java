package fr.esgi.grp9.uparserbackend.mail.domain;

import fr.esgi.grp9.uparserbackend.mail.domain.Email;

public interface EmailService {
    void sendResetPasswordEmail(Email email);
}
