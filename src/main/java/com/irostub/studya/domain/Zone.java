package com.irostub.studya.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Zone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String localNameOfCity;

    @Column(nullable = true)
    private String province;
}
