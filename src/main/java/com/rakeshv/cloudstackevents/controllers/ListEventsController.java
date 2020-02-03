package com.rakeshv.cloudstackevents.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Alert;
import com.rakeshv.cloudstackevents.models.Event;
import com.rakeshv.cloudstackevents.models.EventResponse;
import com.rakeshv.cloudstackevents.models.ListEventsResponse;
import com.rakeshv.cloudstackevents.service.CloudstackAlertService;
import com.rakeshv.cloudstackevents.service.CloudstackEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class ListEventsController {
    @Autowired
    private CloudstackEventService cloudstackEventService;
    @Autowired
    private CloudstackAlertService cloudstackAlertService;

    List<Alert> allAlerts = new ArrayList<>();

    @GetMapping("/events")
    public ResponseEntity<List<String>> listEvents() throws IOException {
        Map<String, String> result = cloudstackEventService.listEvents();
        List<Event> eventList = new ArrayList<>();
        List<Alert> alertList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String key : result.keySet()) {
            ListEventsResponse listEventsResponse = mapper.readValue(result.get(key), ListEventsResponse.class);
            if (listEventsResponse != null) {
                EventResponse eventResponse = listEventsResponse.getListeventsresponse();
                eventList = eventResponse.getEvent();
                eventList.forEach(event -> {
                    resultList.add(key + " DOMAIN:" + event.getDomain() + " " +
                            event.getCreated() + " DESCRIPTION:" +
                            event.getDescription() + " TYPE:" + event.getType());
                });
            }
        }
        return ResponseEntity.ok(resultList);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Alert> listAlertsStream() throws IOException {
        Flux<Alert> flux = Flux.fromIterable(allAlerts);
        return flux;
    }
}
