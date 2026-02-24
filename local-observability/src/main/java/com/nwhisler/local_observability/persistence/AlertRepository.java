package com.nwhisler.local_observability.persistence;

import com.nwhisler.local_observability.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByEnabledTrue();
}