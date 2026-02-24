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

import com.nwhisler.local_observability.persistence.EventRepository;
import com.nwhisler.local_observability.api.EventSearchResponse;
import com.nwhisler.local_observability.api.EventRequest;
import com.nwhisler.local_observability.domain.Event;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.UUID;

import java.time.OffsetDateTime;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventControllerTest {

   @Autowired
   private TestRestTemplate rest;

   @Autowired
   private EventRepository repo;

   @Container 
   @ServiceConnection 
   static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:16");

   @BeforeEach
   void cleanDb() {
      repo.deleteAll();
   }

   @Test 
   void PostAndGetTest() {

      EventRequest req = new EventRequest();
      req.setLevel("INFO");
      req.setMessage("test event");
      req.setService(("orders"));

      ResponseEntity<Event> postResp = rest.postForEntity("/events", req, Event.class);
      assertThat(postResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(postResp.getBody()).isNotNull();
      assertThat(postResp.getBody().getId()).isNotNull();
      assertThat(postResp.getBody().getTs()).isNotNull();
      assertThat(postResp.getBody().getService()).isEqualTo("orders");
      assertThat(postResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(postResp.getBody().getMessage()).isEqualTo("test event");

      UUID createdID = postResp.getBody().getId();
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=10", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().isFirst()).isTrue();
      assertThat(getResp.getBody().isLast()).isTrue();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(createdID);
   }

   @Test 
   void serviceTest() {

      EventRequest ordersReq = new EventRequest();
      ordersReq.setLevel("INFO");
      ordersReq.setMessage("test event");
      ordersReq.setService(("orders"));

      EventRequest billingReq = new EventRequest();
      billingReq.setLevel("INFO");
      billingReq.setMessage("test event");
      billingReq.setService(("billing"));

      ResponseEntity<Event> ordersPostResp = rest.postForEntity("/events", ordersReq, Event.class);
      assertThat(ordersPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(ordersPostResp.getBody()).isNotNull();
      assertThat(ordersPostResp.getBody().getId()).isNotNull();
      assertThat(ordersPostResp.getBody().getTs()).isNotNull();
      assertThat(ordersPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(ordersPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(ordersPostResp.getBody().getMessage()).isEqualTo("test event");

      ResponseEntity<Event> errorPostResp = rest.postForEntity("/events", billingReq, Event.class);
      assertThat(errorPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(errorPostResp.getBody()).isNotNull();
      assertThat(errorPostResp.getBody().getId()).isNotNull();
      assertThat(errorPostResp.getBody().getTs()).isNotNull();
      assertThat(errorPostResp.getBody().getService()).isEqualTo("billing");
      assertThat(errorPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(errorPostResp.getBody().getMessage()).isEqualTo("test event");

      UUID ordersCreatedID = ordersPostResp.getBody().getId();
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=10&service=orders", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(ordersCreatedID);
      assertThat(getResp.getBody().getItems().get(0).getService()).isEqualTo("orders");
   }

   @Test 
   void levelTest() {

      EventRequest infoReq = new EventRequest();
      infoReq.setLevel("INFO");
      infoReq.setMessage("test event");
      infoReq.setService(("orders"));

      EventRequest errorReq = new EventRequest();
      errorReq.setLevel("ERROR");
      errorReq.setMessage("test event");
      errorReq.setService(("orders"));

      ResponseEntity<Event> infoPostResp = rest.postForEntity("/events", infoReq, Event.class);
      assertThat(infoPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(infoPostResp.getBody()).isNotNull();
      assertThat(infoPostResp.getBody().getId()).isNotNull();
      assertThat(infoPostResp.getBody().getTs()).isNotNull();
      assertThat(infoPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(infoPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(infoPostResp.getBody().getMessage()).isEqualTo("test event");

      ResponseEntity<Event> errorPostResp = rest.postForEntity("/events", errorReq, Event.class);
      assertThat(errorPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(errorPostResp.getBody()).isNotNull();
      assertThat(errorPostResp.getBody().getId()).isNotNull();
      assertThat(errorPostResp.getBody().getTs()).isNotNull();
      assertThat(errorPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(errorPostResp.getBody().getLevel()).isEqualTo("ERROR");
      assertThat(errorPostResp.getBody().getMessage()).isEqualTo("test event");

      UUID errorCreatedID = errorPostResp.getBody().getId();
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=10&level=ERROR", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(errorCreatedID);
      assertThat(getResp.getBody().getItems().get(0).getLevel()).isEqualTo("ERROR");
   }

   @Test 
   void messageTest() {

      EventRequest testEventReq = new EventRequest();
      testEventReq.setLevel("INFO");
      testEventReq.setMessage("test event");
      testEventReq.setService(("orders"));

      EventRequest messageReq = new EventRequest();
      messageReq.setLevel("INFO");
      messageReq.setMessage("message");
      messageReq.setService(("orders"));

      ResponseEntity<Event> testEventPostResp = rest.postForEntity("/events", testEventReq, Event.class);
      assertThat(testEventPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(testEventPostResp.getBody()).isNotNull();
      assertThat(testEventPostResp.getBody().getId()).isNotNull();
      assertThat(testEventPostResp.getBody().getTs()).isNotNull();
      assertThat(testEventPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(testEventPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(testEventPostResp.getBody().getMessage()).isEqualTo("test event");

      ResponseEntity<Event> messageResp = rest.postForEntity("/events", messageReq, Event.class);
      assertThat(messageResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(messageResp.getBody()).isNotNull();
      assertThat(messageResp.getBody().getId()).isNotNull();
      assertThat(messageResp.getBody().getTs()).isNotNull();
      assertThat(messageResp.getBody().getService()).isEqualTo("orders");
      assertThat(messageResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(messageResp.getBody().getMessage()).isEqualTo("message");

      UUID testEventCreatedID = testEventPostResp.getBody().getId();
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=10&q=test", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(testEventCreatedID);
      assertThat(getResp.getBody().getItems().get(0).getMessage()).isEqualTo("test event");
   }


   @Test 
   void toAndFromTest() throws Exception{

      EventRequest firstReq = new EventRequest();
      firstReq.setLevel("INFO");
      firstReq.setMessage("test event");
      firstReq.setService(("orders"));

      EventRequest secondReq = new EventRequest();
      secondReq.setLevel("ERROR");
      secondReq.setMessage("message");
      secondReq.setService(("logging"));

      EventRequest thirdReq = new EventRequest();
      thirdReq.setLevel("STATS");
      thirdReq.setMessage("statisitcs");
      thirdReq.setService(("data"));

      ResponseEntity<Event> firstPostResp = rest.postForEntity("/events", firstReq, Event.class);
      assertThat(firstPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(firstPostResp.getBody()).isNotNull();
      assertThat(firstPostResp.getBody().getId()).isNotNull();
      assertThat(firstPostResp.getBody().getTs()).isNotNull();
      assertThat(firstPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(firstPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(firstPostResp.getBody().getMessage()).isEqualTo("test event");
      TimeUnit.SECONDS.sleep(3);

      ResponseEntity<Event> secondPostResp = rest.postForEntity("/events", secondReq, Event.class);
      assertThat(secondPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(secondPostResp.getBody()).isNotNull();
      assertThat(secondPostResp.getBody().getId()).isNotNull();
      assertThat(secondPostResp.getBody().getTs()).isNotNull();
      OffsetDateTime ts = secondPostResp.getBody().getTs();
      OffsetDateTime from = ts.minusSeconds(1);
      OffsetDateTime to = ts.plusSeconds(1);
      assertThat(secondPostResp.getBody().getService()).isEqualTo("logging");
      assertThat(secondPostResp.getBody().getLevel()).isEqualTo("ERROR");
      assertThat(secondPostResp.getBody().getMessage()).isEqualTo("message");
      TimeUnit.SECONDS.sleep(3);

      ResponseEntity<Event> thirdPostResp = rest.postForEntity("/events", thirdReq, Event.class);
      assertThat(thirdPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(thirdPostResp.getBody()).isNotNull();
      assertThat(thirdPostResp.getBody().getId()).isNotNull();
      assertThat(thirdPostResp.getBody().getTs()).isNotNull();
      assertThat(thirdPostResp.getBody().getService()).isEqualTo("data");
      assertThat(thirdPostResp.getBody().getLevel()).isEqualTo("STATS");
      assertThat(thirdPostResp.getBody().getMessage()).isEqualTo("statisitcs");

      UUID timestampeUUID = secondPostResp.getBody().getId();
      String url = String.format("/events?page=0&size=10&from=%s&to=%s", from.toString(), to.toString());
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity(url, EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(timestampeUUID);
      assertThat(getResp.getBody().getItems().get(0).getMessage()).isEqualTo("message");
      assertThat(getResp.getBody().getItems().get(0).getLevel()).isEqualTo("ERROR");
      assertThat(getResp.getBody().getItems().get(0).getService()).isEqualTo("logging");
   }

   @Test 
   void tsAndIdTest() throws Exception{

      EventRequest firstReq = new EventRequest();
      firstReq.setLevel("INFO");
      firstReq.setMessage("test event");
      firstReq.setService(("orders"));

      EventRequest secondReq = new EventRequest();
      secondReq.setLevel("ERROR");
      secondReq.setMessage("message");
      secondReq.setService(("logging"));

      EventRequest thirdReq = new EventRequest();
      thirdReq.setLevel("STATS");
      thirdReq.setMessage("statisitcs");
      thirdReq.setService(("data"));

      ResponseEntity<Event> firstPostResp = rest.postForEntity("/events", firstReq, Event.class);
      assertThat(firstPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(firstPostResp.getBody()).isNotNull();
      assertThat(firstPostResp.getBody().getId()).isNotNull();
      assertThat(firstPostResp.getBody().getTs()).isNotNull();
      assertThat(firstPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(firstPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(firstPostResp.getBody().getMessage()).isEqualTo("test event");
      TimeUnit.SECONDS.sleep(3);

      ResponseEntity<Event> secondPostResp = rest.postForEntity("/events", secondReq, Event.class);
      assertThat(secondPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(secondPostResp.getBody()).isNotNull();
      assertThat(secondPostResp.getBody().getId()).isNotNull();
      assertThat(secondPostResp.getBody().getTs()).isNotNull();
      assertThat(secondPostResp.getBody().getService()).isEqualTo("logging");
      assertThat(secondPostResp.getBody().getLevel()).isEqualTo("ERROR");
      assertThat(secondPostResp.getBody().getMessage()).isEqualTo("message");

      ResponseEntity<Event> thirdPostResp = rest.postForEntity("/events", thirdReq, Event.class);
      assertThat(thirdPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(thirdPostResp.getBody()).isNotNull();
      assertThat(thirdPostResp.getBody().getId()).isNotNull();
      assertThat(thirdPostResp.getBody().getTs()).isNotNull();
      assertThat(thirdPostResp.getBody().getService()).isEqualTo("data");
      assertThat(thirdPostResp.getBody().getLevel()).isEqualTo("STATS");
      assertThat(thirdPostResp.getBody().getMessage()).isEqualTo("statisitcs");

      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=10", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(3);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(3);

      List<Event> items = getResp.getBody().getItems();
      for(int idx = 1; idx < items.size(); idx++) {
         OffsetDateTime prev_ts = items.get(idx - 1).getTs();
         OffsetDateTime curr_ts = items.get(idx).getTs();

         UUID prev_id = items.get(idx - 1).getId();
         UUID curr_id = items.get(idx).getId();
         boolean ordered = (
            curr_ts.isAfter(prev_ts) || (
               curr_ts.isEqual(prev_ts) &&
               curr_id.compareTo(prev_id) >= 0
            )

         );
         assertThat(ordered);
      }
   }

   @Test 
   void pageOverlapTest() {

      EventRequest firstReq = new EventRequest();
      firstReq.setLevel("INFO");
      firstReq.setMessage("test event");
      firstReq.setService(("orders"));

      EventRequest secondReq = new EventRequest();
      secondReq.setLevel("ERROR");
      secondReq.setMessage("message");
      secondReq.setService(("logging"));

      EventRequest thirdReq = new EventRequest();
      thirdReq.setLevel("STATS");
      thirdReq.setMessage("statisitcs");
      thirdReq.setService(("data"));

      ResponseEntity<Event> firstPostResp = rest.postForEntity("/events", firstReq, Event.class);
      assertThat(firstPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(firstPostResp.getBody()).isNotNull();
      assertThat(firstPostResp.getBody().getId()).isNotNull();
      assertThat(firstPostResp.getBody().getTs()).isNotNull();
      assertThat(firstPostResp.getBody().getService()).isEqualTo("orders");
      assertThat(firstPostResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(firstPostResp.getBody().getMessage()).isEqualTo("test event");

      ResponseEntity<Event> secondPostResp = rest.postForEntity("/events", secondReq, Event.class);
      assertThat(secondPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(secondPostResp.getBody()).isNotNull();
      assertThat(secondPostResp.getBody().getId()).isNotNull();
      assertThat(secondPostResp.getBody().getTs()).isNotNull();
      assertThat(secondPostResp.getBody().getService()).isEqualTo("logging");
      assertThat(secondPostResp.getBody().getLevel()).isEqualTo("ERROR");
      assertThat(secondPostResp.getBody().getMessage()).isEqualTo("message");

      ResponseEntity<Event> thirdPostResp = rest.postForEntity("/events", thirdReq, Event.class);
      assertThat(thirdPostResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(thirdPostResp.getBody()).isNotNull();
      assertThat(thirdPostResp.getBody().getId()).isNotNull();
      assertThat(thirdPostResp.getBody().getTs()).isNotNull();
      assertThat(thirdPostResp.getBody().getService()).isEqualTo("data");
      assertThat(thirdPostResp.getBody().getLevel()).isEqualTo("STATS");
      assertThat(thirdPostResp.getBody().getMessage()).isEqualTo("statisitcs");

      ResponseEntity<EventSearchResponse> getFirstResp = rest.getForEntity("/events?page=0&size=2", EventSearchResponse.class);
      assertThat(getFirstResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getFirstResp.getBody()).isNotNull();
      assertThat(getFirstResp.getBody().getItems().size()).isEqualTo(2);
      assertThat(getFirstResp.getBody().getTotalItems()).isEqualTo(3);
      assertThat(getFirstResp.getBody().isFirst()).isTrue();
      assertThat(getFirstResp.getBody().isLast()).isFalse();

      ResponseEntity<EventSearchResponse> getSecondResp = rest.getForEntity("/events?page=1&size=2", EventSearchResponse.class);
      assertThat(getSecondResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getSecondResp.getBody()).isNotNull();
      assertThat(getSecondResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getSecondResp.getBody().getTotalItems()).isEqualTo(3);
      assertThat(getSecondResp.getBody().isFirst()).isFalse();
      assertThat(getSecondResp.getBody().isLast()).isTrue();

   }

   @Test 
   void sizeCapTest() {

      EventRequest req = new EventRequest();
      req.setLevel("INFO");
      req.setMessage("test event");
      req.setService(("orders"));

      ResponseEntity<Event> postResp = rest.postForEntity("/events", req, Event.class);
      assertThat(postResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(postResp.getBody()).isNotNull();
      assertThat(postResp.getBody().getId()).isNotNull();
      assertThat(postResp.getBody().getTs()).isNotNull();
      assertThat(postResp.getBody().getService()).isEqualTo("orders");
      assertThat(postResp.getBody().getLevel()).isEqualTo("INFO");
      assertThat(postResp.getBody().getMessage()).isEqualTo("test event");

      UUID createdID = postResp.getBody().getId();
      ResponseEntity<EventSearchResponse> getResp = rest.getForEntity("/events?page=0&size=2000", EventSearchResponse.class);
      assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();
      assertThat(getResp.getBody()).isNotNull();
      assertThat(getResp.getBody().isFirst()).isTrue();
      assertThat(getResp.getBody().isLast()).isTrue();
      assertThat(getResp.getBody().getItems().size()).isEqualTo(1);
      assertThat(getResp.getBody().getTotalItems()).isEqualTo(1);
      assertThat(getResp.getBody().getItems().get(0).getId()).isEqualTo(createdID);
      assertThat(getResp.getBody().getSize()).isEqualTo(200);
   }

}