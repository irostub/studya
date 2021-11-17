package com.irostub.studya.modules.account;


import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.zone.Zone;

import java.util.List;
import java.util.Set;

public interface AccountRepositorySupport {
    List<Account> findByTagsAndZones(Set<Tag> tags, Set<Zone> zones);
}
