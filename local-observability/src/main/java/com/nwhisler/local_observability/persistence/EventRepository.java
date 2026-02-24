package com.nwhisler.local_observability.persistence;

import com.nwhisler.local_observability.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository
        extends JpaRepository<Event, UUID>,
                JpaSpecificationExecutor<Event> {
}
