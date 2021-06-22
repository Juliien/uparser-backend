package fr.esgi.grp9.uparserbackend.mail.domain;

import fr.esgi.grp9.uparserbackend.user.domain.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void sendResetPasswordEmail(Email email) {
        String code = this.userService.getCode(email.getMailTo());
        String content = " If you don't request a new password for Uparser, please ignore this e-mail. " +
                "%n You have requested a new password, and here it is the verification code: %n" +
                code +
                "%n Do not reply to this e-mail address, the messages won't be replied to. " +
                "%n Sincerely yours, Uparser Team.";

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
