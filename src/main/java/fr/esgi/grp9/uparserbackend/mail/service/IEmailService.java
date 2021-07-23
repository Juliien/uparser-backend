package fr.esgi.grp9.uparserbackend.mail.service;

import fr.esgi.grp9.uparserbackend.mail.domain.Email;

public interface IEmailService {
    void sendResetPasswordEmail(Email email);
}
