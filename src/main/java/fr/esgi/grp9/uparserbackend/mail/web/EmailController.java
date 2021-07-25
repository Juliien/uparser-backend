package fr.esgi.grp9.uparserbackend.mail.web;

import fr.esgi.grp9.uparserbackend.mail.domain.Email;
import fr.esgi.grp9.uparserbackend.mail.service.EmailService;
import fr.esgi.grp9.uparserbackend.mail.service.IEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity sendEmailToUser(@RequestBody Email email) {
        try {
            this.emailService.sendResetPasswordEmail(email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
