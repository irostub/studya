package com.irostub.studya.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class ConsoleMailService implements MailService {
    @Override
    public void send(MailMessage e) {
        log.info("[ConsoleMailService] to={}, subject={}, text={}", e.getTo(), e.getSubject(), e.getText());
    }
}
