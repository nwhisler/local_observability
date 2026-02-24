package com.nwhisler.local_observability.api;

import com.nwhisler.local_observability.domain.Event;
import com.nwhisler.local_observability.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody EventRequest req) {
        return service.create(req);
    }

    @GetMapping
    public EventSearchResponse search(@ModelAttribute EventSearchRequest req) {
        Page<Event> page = service.search(req);
        return EventSearchResponse.from(page);
    }
}