package fr.esgi.grp9.uparserbackend.mail.domain;

import fr.esgi.grp9.uparserbackend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Override
    public void sendResetPasswordEmail(Email email) {
        String code = this.userService.getCode(email.getMailTo());
        String content = "Réinitialisation de votre mot de passe\n" +
                "\nBonjour\n"+
                "\nNous avons bien reçu votre demande de changement de mot de passe.\n " +
                "\nVous trouverez ci-dessous le code de vérification:\n" +
                "\n" + code + "\n" +
                "\nSi vous n'avez pas demandé à réinitialiser votre mot de passe, vous pouvez ignorer cet e-mail.\n" +
                "\nA très bientôt,\n" +
                "\nL'équipe Uparser";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(email.getMailSubject());
            mimeMessageHelper.setFrom(email.getMailFrom());
            mimeMessageHelper.setTo(email.getMailTo());
            mimeMessageHelper.setText(content);

            mailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
