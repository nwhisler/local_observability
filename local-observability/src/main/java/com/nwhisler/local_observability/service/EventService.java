package com.nwhisler.local_observability.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nwhisler.local_observability.api.EventRequest;
import com.nwhisler.local_observability.domain.Event;
import com.nwhisler.local_observability.persistence.EventRepository;
import com.nwhisler.local_observability.api.EventSearchResponse;

import com.nwhisler.local_observability.api.EventSearchRequest;
import com.nwhisler.local_observability.persistence.EventSpecifications;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event create(EventRequest req) {
        
        String level = req.getLevel();
        String service = req.getService();
        String message = req.getMessage();
        Map<String, Object> attrs = req.getAttrs();

        Event e = new Event();
        e.setId(UUID.randomUUID());
        e.setTs(OffsetDateTime.now());
        e.setLevel(level == null ? null : level.trim());
        e.setService(service == null ? null : service.trim());
        e.setMessage(message);
        e.setAttrs(attrs == null ? Map.of() : attrs);
        return repo.save(e);
    }

    private void normalizeRequest(EventSearchRequest req) {
        
        OffsetDateTime from = req.getFrom();
        OffsetDateTime to = req.getTo();
        String service = req.getService();
        String level = req.getLevel();
        String q = req.getQ();
        int page = req.getPage();
        int size = req.getSize();

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from must be <= to");
        }
        if(page < 0){
            req.setPage(0);
        }
        if(size<=0) {
            req.setSize((50));
        }
        if (size > 200) {
            req.setSize(200);
        }
        service = (service == null) ? null : service.trim();
        level = (level == null) ? null : level.trim();
        q = (q == null) ? null : q.trim();

        req.setService(service);
        req.setLevel(level);
        req.setQ(q);

    }

    public Page<Event> search(EventSearchRequest req) 
    {
        normalizeRequest(req);

        OffsetDateTime from = req.getFrom();
        OffsetDateTime to = req.getTo();
        String service = req.getService();
        String level = req.getLevel();
        String q = req.getQ();
        int page = req.getPage();
        int size = req.getSize();

        Sort sort = Sort.by(Sort.Direction.ASC, "ts")
                        .and(Sort.by(Sort.Direction.ASC, "id"));
        PageRequest pr = PageRequest.of(page, size, sort);
        Specification<Event> spec = Specification.<Event>where(null)
                                    .and(EventSpecifications.tsGte(from))
                                    .and(EventSpecifications.tsLte(to))
                                    .and(EventSpecifications.hasService(service))
                                    .and(EventSpecifications.hasLevel(level))
                                    .and(EventSpecifications.messageContains(q));

        return repo.findAll(spec, pr); 
    }

    

}