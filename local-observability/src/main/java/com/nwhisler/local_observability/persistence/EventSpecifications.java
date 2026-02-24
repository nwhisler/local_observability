package com.nwhisler.local_observability.persistence;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;
import com.nwhisler.local_observability.domain.Event;

public final class EventSpecifications {

    private EventSpecifications() {}

    public static Specification<Event> hasService(String service) {
        if (service == null || service.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("service"), service);
    }
    public static Specification<Event> tsGte(OffsetDateTime from) {
        if (from == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ts"), from);
    }

    public static Specification<Event> tsLte(OffsetDateTime to) {
        if (to == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("ts"), to);
    }

    public static Specification<Event> hasLevel(String level) { 
        if (level == null || level.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }

    public static Specification<Event> messageContains(String q) { 
        if ( q == null || q.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("message")), pattern);
    }
}