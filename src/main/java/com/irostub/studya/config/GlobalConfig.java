package com.irostub.studya.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Getter
@Profile("dev")
@Configuration
@PropertySources({
        @PropertySource(value="file:c:/dev/studya/config.properties", ignoreResourceNotFound = true),
        @PropertySource(value="file:${user.home}/Documents/dev/studya/config.properties", ignoreResourceNotFound = true)
})
public class GlobalConfig {
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;
}
