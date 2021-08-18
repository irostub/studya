package com.irostub.studya.service;

import com.irostub.studya.controller.form.ZoneForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Zone;
import com.irostub.studya.repository.AccountRepository;
import com.irostub.studya.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final AccountRepository accountRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zones);
        }
    }

    public List<Zone> loadZones() {
        return zoneRepository.findAll();
    }

    public void addZone(Account account, ZoneForm zoneForm) {
        String zoneTitle = zoneForm.getZoneTitle();
        int i = zoneTitle.indexOf('(');
        String city = zoneTitle.substring(0, i);
        Zone zone = zoneRepository.findByCity(city).orElseThrow(IllegalArgumentException::new);
        accountRepository.findById(account.getId()).orElseThrow(IllegalArgumentException::new).getZones().add(zone);
    }

    public Set<Zone> loadAccountZones(Account account) {
        return accountRepository.findById(account.getId()).orElseThrow(IllegalArgumentException::new).getZones();
    }
}
