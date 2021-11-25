package com.irostub.studya.modules.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class PersistentLogins {
    @NotNull
    @Column(nullable = false, length = 64)
    private String username;

    @Id
    @Column(length = 64)
    private String series;

    @NotNull
    @Column(nullable = false, length = 64)
    private String token;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime lastUsed;
}
