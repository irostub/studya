package com.irostub.studya.service.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailMessage {
    private String to;
    private String subject;
    private String text;
}
