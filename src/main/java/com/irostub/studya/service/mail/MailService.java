package com.irostub.studya.service.mail;

import javax.mail.MessagingException;

public interface MailService {
    void send(MailMessage e) throws MessagingException;
}
