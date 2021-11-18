package com.irostub.studya.modules.account;

import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.zone.Zone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.irostub.studya.modules.account.QAccount.account;

@RequiredArgsConstructor
public class AccountRepositorySupportImpl implements AccountRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Account> findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        return jpaQueryFactory.
                selectFrom(account)
                .where(account.tags.any().in(tags), account.zones.any().in(zones))
                .fetch();
    }
}
