package com.irostub.studya.modules.study;

import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.account.UserAccount;
import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.zone.Zone;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(
        name = "Study.withAllRelations",
        attributeNodes = {
                @NamedAttributeNode("managers"),
                @NamedAttributeNode("members"),
                @NamedAttributeNode("tags"),
                @NamedAttributeNode("zones")
        }
)
@NamedEntityGraph(
        name = "Study.withMembers",
        attributeNodes = {
                @NamedAttributeNode("members"),
        }
)
@NamedEntityGraph(
        name = "Study.withManagers",
        attributeNodes = {
                @NamedAttributeNode("managers")
        }
)
@NamedEntityGraph(
        name = "Study.withTags",
        attributeNodes = {
                @NamedAttributeNode("tags")
        }
)
@NamedEntityGraph(
        name = "Study.withZones",
        attributeNodes = {
                @NamedAttributeNode("zones")
        }
)
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    @Length(min = 2, max = 100)
    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToMany
    private Set<Zone> zones;

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    @ColumnDefault("0")
    private int memberCount;

    public void close() {
        if (published && !closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 종료할 수 없습니다.");
        }
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public boolean isRemovable() {
        return !published;
    }

    public String getEncodedPath() {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }
}
