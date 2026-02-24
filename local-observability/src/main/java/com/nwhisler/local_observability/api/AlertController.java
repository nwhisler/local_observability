package com.nwhisler.local_observability.api;

import com.nwhisler.local_observability.api.AlertEvaluationResponse;
import com.nwhisler.local_observability.api.AlertRequest;
import com.nwhisler.local_observability.domain.Alert;
import com.nwhisler.local_observability.service.AlertService;
import com.nwhisler.local_observability.domain.AlertEvaluation;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    
    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Alert create(@Valid @RequestBody AlertRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<AlertResponse> list() {
        return service.list()
                .stream()
                .map(AlertResponse::from)
                .toList();
    }

    @PostMapping("/{id}/test")
    public AlertEvaluationResponse evaluate(@PathVariable UUID id) {
        
        AlertEvaluation ae = service.evaluate(id);
        return AlertEvaluationResponse.from(ae);
    }

}
