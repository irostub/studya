package com.irostub.studya.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev")
@RequiredArgsConstructor
@Component
public class HtmlMailService implements MailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void send(MailMessage e) throws MessagingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setTo(e.getTo());
            mimeMessageHelper.setSubject(e.getSubject());
            mimeMessageHelper.setText(e.getText(), true);
            javaMailSender.send(mimeMessage);
        }catch (MessagingException messagingException) {
            log.error("[HtmlMailService.send] MessagingException",messagingException);
            throw new MessagingException(messagingException.toString());
        }
    }
}
