package com.example.backendapi.Service;

import com.example.backendapi.Abstractions.IEmailService;
import com.example.backendapi.Model.AppSettings;
import com.example.backendapi.Model.User;
import com.example.backendapi.Model.VerificationToken;
import com.example.backendapi.ModelMapping.ExchangeBook;
import com.example.backendapi.Repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class EmailService implements IEmailService {
    @Autowired
    private Environment environment;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Override
    public void sendMailVerification(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = appSettings.getEmail();
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        VerificationToken token = verificationTokenRepository.findByUser(user);
        String verifyURL = siteURL + "verify?code=" + token.getToken();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
    public void sendMailExchangeBook(ExchangeBook exchangeBook) throws MessagingException, UnsupportedEncodingException {
        String toAddress = exchangeBook.getEmailBookGiver();
        String fromAddress = appSettings.getEmail();
        String subject = "Please confirm book exchange";
        String content = "Dear [[NAME]],<br>"
                + "Request to exchange books, name:[[NAME_BOOK]]<br>"
                +"This is information of book recipient:<br>"
                +"name recipient: [[NAME_RECIPIENT]]<br>"
                +"email: [[EMAIL_RECIPIENT]]<br>"
                +"phoneNumber: [[PHONE_NUMBER_RECIPIENT]]<br>"
                + "Thank you,<br>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[NAME]]", exchangeBook.getUserNameBookGiver());
        content = content.replace("[[NAME_BOOK]]", exchangeBook.getNameBook());
        content = content.replace("[[NAME_RECIPIENT]]", exchangeBook.getUserNameBookRecipient());
        content = content.replace("[[EMAIL_RECIPIENT]]", exchangeBook.getEmailBookRecipient());
        content = content.replace("[[PHONE_NUMBER_RECIPIENT]]", exchangeBook.getPhoneNumberBookRecipient());



        helper.setText(content, true);

        mailSender.send(message);
    }
}
