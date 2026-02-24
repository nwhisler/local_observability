package com.nwhisler.local_observability.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.nwhisler.local_observability.persistence.AlertRepository;
import com.nwhisler.local_observability.service.AlertService;
import com.nwhisler.local_observability.domain.Alert;
import com.nwhisler.local_observability.domain.AlertEvaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ConditionalOnProperty(
    name = "observability.alerts.scheduler.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@Component
public class AlertScheduler {

    private final AlertService service;
    private final AlertRepository repo;
    private static final Logger log = LoggerFactory.getLogger(AlertScheduler.class);

    public AlertScheduler(AlertService service, AlertRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    private void logTriggeredAlert(Alert alert, AlertEvaluation eval) {
        
        log.warn(
            "ALERT TRIGGERED id={} name={} count={} windowStart={} windowEnd={}",
            alert.getId(),
            alert.getName(),
            eval.getMatchingEventCount(),
            eval.getWindowStart(),
            eval.getWindowEnd()
        );
    }
  
    @Scheduled(fixedDelayString = "${observability.alerts.scheduler.delay-ms:10000}")
    public void evaluateAlerts() {

        List<Alert> alerts = repo.findByEnabledTrue();
        for(Alert alert:alerts) {
            try {
                UUID id = alert.getId();
                AlertEvaluation eval = service.evaluate(id);
                if(eval.isTriggered()){
                    logTriggeredAlert(alert, eval);
                }
            }
            catch (Exception e) {
                log.error(
                    "Failed to evaluate alert id={} name={}",
                    alert.getId(),
                    alert.getName(),
                    e
                );
            }
        }
    }

}
