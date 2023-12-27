package com.pikachu.constdu.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;

    public boolean sendEmail(String clientEmail, String subject, String content, String attachmentFileName, File file){
        MimeMessage mm =  mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mm, true);
            helper.setTo(clientEmail);
            helper.setFrom(env.getProperty("spring.mail.username"));
            helper.setSubject(subject);
            helper.setText(content);
            helper.addAttachment(attachmentFileName, file);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        mailSender.send(mm);
        return true;
    }
    public boolean sendEmail(String clientEmail, String subject, String content){
        MimeMessage mm =  mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mm, true);
            helper.setTo(clientEmail);
            helper.setFrom(env.getProperty("spring.mail.username"));
            helper.setSubject(subject);
            helper.setText(content,true);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        mailSender.send(mm);
        return true;
    }

}
