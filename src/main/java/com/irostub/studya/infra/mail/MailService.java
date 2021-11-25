package com.irostub.studya.infra.mail;

import javax.mail.MessagingException;

public interface MailService {
    void send(MailMessage e);
}
