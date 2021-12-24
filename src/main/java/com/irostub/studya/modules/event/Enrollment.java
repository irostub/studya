package com.irostub.studya.modules.event;

import com.irostub.studya.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@NamedEntityGraph(
        name="Enrollment.withEventAndStudy",
        attributeNodes={
                @NamedAttributeNode(value="event", subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name="study", attributeNodes = @NamedAttributeNode("study"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;
}
