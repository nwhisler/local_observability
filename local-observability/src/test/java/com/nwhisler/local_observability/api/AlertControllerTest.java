package com.nwhisler.local_observability.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.Transient;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import com.nwhisler.local_observability.persistence.AlertRepository;
import com.nwhisler.local_observability.api.AlertRequest;
import com.nwhisler.local_observability.api.AlertEvaluationResponse;
import com.nwhisler.local_observability.api.AlertResponse;

import com.nwhisler.local_observability.persistence.EventRepository;

import com.nwhisler.local_observability.api.EventSearchResponse;
import com.nwhisler.local_observability.api.AlertRequest;
import com.nwhisler.local_observability.api.EventRequest;
import com.nwhisler.local_observability.domain.Alert;
import com.nwhisler.local_observability.domain.AlertEvaluation;
import com.nwhisler.local_observability.domain.Event;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.UUID;

import java.time.OffsetDateTime;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlertControllerTest {

   @Autowired
   private TestRestTemplate rest;

   @Autowired
   private EventRepository eventRepo;

   @Autowired
   private AlertRepository alertRepo;

   @Container 
   @ServiceConnection 
   static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:16");

   @BeforeEach
   void cleanDb() {
      eventRepo.deleteAll();
      alertRepo.deleteAll();
   }

   @Test 
   void alertTest() throws Exception {
      
        EventRequest req = new EventRequest();
        req.setLevel("ERROR");
        req.setMessage("test event");
        req.setService(("orders"));

        ResponseEntity<Event> firstPostResp = rest.postForEntity("/events", req, Event.class);
        assertThat(firstPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(firstPostResp.getBody()).isNotNull();
        assertThat(firstPostResp.getBody().getId()).isNotNull();
        assertThat(firstPostResp.getBody().getTs()).isNotNull();
        assertThat(firstPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(firstPostResp.getBody().getLevel()).isEqualTo("ERROR");
        assertThat(firstPostResp.getBody().getMessage()).isEqualTo("test event");

        ResponseEntity<Event> secodPostResp = rest.postForEntity("/events", req, Event.class);
        assertThat(secodPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(secodPostResp.getBody()).isNotNull();
        assertThat(secodPostResp.getBody().getId()).isNotNull();
        assertThat(secodPostResp.getBody().getTs()).isNotNull();
        assertThat(secodPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(secodPostResp.getBody().getLevel()).isEqualTo("ERROR");
        assertThat(secodPostResp.getBody().getMessage()).isEqualTo("test event");
        TimeUnit.SECONDS.sleep(1);

        ResponseEntity<Event> thirdPostResp = rest.postForEntity("/events", req, Event.class);
        assertThat(thirdPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(thirdPostResp.getBody()).isNotNull();
        assertThat(thirdPostResp.getBody().getId()).isNotNull();
        assertThat(thirdPostResp.getBody().getTs()).isNotNull();
        assertThat(thirdPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(thirdPostResp.getBody().getLevel()).isEqualTo("ERROR");
        assertThat(thirdPostResp.getBody().getMessage()).isEqualTo("test event");
        TimeUnit.SECONDS.sleep(1);

        ResponseEntity<Event> fourthPostResp = rest.postForEntity("/events", req, Event.class);
        assertThat(fourthPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(fourthPostResp.getBody()).isNotNull();
        assertThat(fourthPostResp.getBody().getId()).isNotNull();
        assertThat(fourthPostResp.getBody().getTs()).isNotNull();
        assertThat(fourthPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(fourthPostResp.getBody().getLevel()).isEqualTo("ERROR");
        assertThat(fourthPostResp.getBody().getMessage()).isEqualTo("test event");
        TimeUnit.SECONDS.sleep(1);

        ResponseEntity<Event> fifthPostResp = rest.postForEntity("/events", req, Event.class);
        assertThat(fifthPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(fifthPostResp.getBody()).isNotNull();
        assertThat(fifthPostResp.getBody().getId()).isNotNull();
        assertThat(fifthPostResp.getBody().getTs()).isNotNull();
        assertThat(fifthPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(fifthPostResp.getBody().getLevel()).isEqualTo("ERROR");
        assertThat(fifthPostResp.getBody().getMessage()).isEqualTo("test event");

        AlertRequest ar = new AlertRequest();
        ar.setName("ErrorAlert");
        ar.setEnabled(true);
        ar.setService("orders");
        ar.setLevel("ERROR");
        ar.setQ("test event");
        ar.setThresholdCount(3);
        ar.setWindowSeconds(3600);
        ar.setCooldownSeconds(300);

        ResponseEntity<AlertResponse> alertFirstPostResp = rest.postForEntity("/alerts", ar, AlertResponse.class);
        assertThat(alertFirstPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(alertFirstPostResp.getBody().getName()).isEqualTo("ErrorAlert"); 
        assertThat(alertFirstPostResp.getBody().getService()).isEqualTo("orders");
        assertThat(alertFirstPostResp.getBody().getLevel()).isEqualTo("ERROR"); 
        assertThat(alertFirstPostResp.getBody().getQ()).isEqualTo("test event");
         
        UUID id = alertFirstPostResp.getBody().getId();
        String url = String.format("/alerts/%s/test", id.toString());
        ResponseEntity<AlertEvaluationResponse> alertSecondPostResp = rest.postForEntity(url, null, AlertEvaluationResponse.class);
        assertThat(alertSecondPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(alertSecondPostResp.getBody().isTriggered()).isTrue();
        assertThat(alertSecondPostResp.getBody().getCount()).isEqualTo(5L);
        assertThat(alertSecondPostResp.getBody().getWindowStart().isBefore(alertSecondPostResp.getBody().getWindowEnd()));

        ResponseEntity<AlertEvaluationResponse> alertThirdPostResp = rest.postForEntity(url, null, AlertEvaluationResponse.class);
        assertThat(alertThirdPostResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(alertThirdPostResp.getBody().isTriggered()).isFalse();
        assertThat(alertThirdPostResp.getBody().getCount()).isEqualTo(5L);
        assertThat(alertThirdPostResp.getBody().getWindowStart().isBefore(alertThirdPostResp.getBody().getWindowEnd()));

   }


}