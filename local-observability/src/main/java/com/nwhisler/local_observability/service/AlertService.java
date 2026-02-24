package com.nwhisler.local_observability.service;

import com.nwhisler.local_observability.api.AlertRequest;
import com.nwhisler.local_observability.domain.Alert;
import com.nwhisler.local_observability.persistence.AlertRepository;
import com.nwhisler.local_observability.domain.AlertEvaluation;
import com.nwhisler.local_observability.persistence.EventSpecifications;
import com.nwhisler.local_observability.domain.Event;
import com.nwhisler.local_observability.persistence.EventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertService {

    private final AlertRepository alertRepo;
    private final EventRepository eventRepo;

    public AlertService(AlertRepository alertRepo, EventRepository eventRepo) {
        this.alertRepo = alertRepo;
        this.eventRepo = eventRepo;
    }    

    public void normalizeCreate(AlertRequest req) {

        AlertRequest ar = new AlertRequest();

        String name = req.getName();
        Boolean enabled = req.isEnabled();
        String service = req.getService();
        String level = req.getLevel();
        String q = req.getQ();
        int thresholdCount = req.getThresholdCount();
        int windowSeconds = req.getWindowSeconds();
        int cooldownSeconds = req.getCooldownSeconds();
        OffsetDateTime created = OffsetDateTime.now();
        OffsetDateTime updated = created;

        name = (name == null ? null : name.trim());
        enabled = (enabled != null ? enabled : true);
        service = (service == null ? null : service.trim());
        level = (level == null ? null : level.trim());
        q = (q == null ? null : q.trim());

        if(windowSeconds < 0) {
            windowSeconds = 0;
        }
        if(cooldownSeconds < 0) {
            cooldownSeconds = 0;
        }

        ar.setName(name);
        ar.setEnabled(enabled);
        ar.setService(service);
        ar.setLevel(level);
        ar.setQ(q);
        ar.setThresholdCount(thresholdCount);
        ar.setWindowSeconds(windowSeconds);
        ar.setCooldownSeconds(cooldownSeconds);

    }

    public Alert create(AlertRequest req) {
        
        normalizeCreate(req);

        String name = req.getName();
        boolean enabled = req.isEnabled();
        String service = req.getService();
        String level = req.getLevel();
        String q = req.getQ();
        int thresholdCount = req.getThresholdCount();
        int windowSeconds = req.getWindowSeconds();
        int cooldownSeconds = req.getCooldownSeconds();
        OffsetDateTime lastTriggered = null;
        OffsetDateTime created = OffsetDateTime.now();
        OffsetDateTime updated = created;

        Alert a = new Alert();
        a.setId(UUID.randomUUID());
        a.setName(name);
        a.setEnabled(enabled);
        a.setService(service);
        a.setLevel(level);
        a.setQ(q);
        a.setThresholdCount(thresholdCount);
        a.setWindowSeconds(windowSeconds);
        a.setCooldownSeconds(cooldownSeconds);
        a.setLastTriggered(lastTriggered);
        a.setCreated(created);
        a.setUpdated(updated);

        return alertRepo.save(a);

    }

    public List<Alert> list() {

        Sort sort = Sort.by(Sort.Direction.ASC, "created")
                        .and(Sort.by(Sort.Direction.ASC, "id"));
        return alertRepo.findAll(sort);
    }

    public AlertEvaluation evaluate(UUID alertId) {
        
        Alert a = alertRepo.findById(alertId)
                    .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        
        String level = a.getLevel();
        String service = a.getService();
        String q = a.getQ();       
        OffsetDateTime windowEnd = OffsetDateTime.now();
        OffsetDateTime windowStart = windowEnd.minusSeconds(a.getWindowSeconds());
        OffsetDateTime lastTriggered = a.getLastTriggered();

        Specification<Event> spec = Specification.<Event>where(null)
                            .and(EventSpecifications.tsGte(windowStart))
                            .and(EventSpecifications.tsLte(windowEnd))
                            .and(EventSpecifications.hasService(service))
                            .and(EventSpecifications.hasLevel(level))
                            .and(EventSpecifications.messageContains(q));

        long matchingEventCount = eventRepo.count(spec);
        boolean triggered = matchingEventCount >= a.getThresholdCount();

        if(lastTriggered != null) {
            Boolean allowedAt = windowEnd.isBefore(lastTriggered.plusSeconds(a.getCooldownSeconds()));
            if(allowedAt) {
                triggered = false;
            }
        }

        if(triggered) {
            a.setLastTriggered(windowEnd);
            alertRepo.save(a);
        }

        return new AlertEvaluation(triggered, matchingEventCount, windowStart, windowEnd);
    }



}