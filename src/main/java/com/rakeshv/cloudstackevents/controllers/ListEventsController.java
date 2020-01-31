package com.rakeshv.cloudstackevents.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Alert;
import com.rakeshv.cloudstackevents.models.AlertResponse;
import com.rakeshv.cloudstackevents.models.Event;
import com.rakeshv.cloudstackevents.models.EventResponse;
import com.rakeshv.cloudstackevents.models.ListAlertsResponse;
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
//    @Scheduled(cron = "0 */2 * * * ?")
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

    @GetMapping(value = "/alerts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @Scheduled(cron = "0 */2 * * * ?")
    public ResponseEntity<List<String>> listAlerts() throws IOException {
        Map<String, String> result = cloudstackAlertService.listAlerts();
        List<Alert> alertList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String key : result.keySet()) {
            ListAlertsResponse listAlertsResponse = mapper.readValue(result.get(key), ListAlertsResponse.class);
            if (listAlertsResponse != null) {
                AlertResponse alertResponse = listAlertsResponse.getListalertsresponse();
                alertList = alertResponse.getAlert();
                alertList.forEach(alert -> {
                    resultList.add(key + " DESCRIPTION: " + alert.getDescription() + " NAME:" + alert.getName() +
                            " TIME: " + alert.getSent());
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

//    @Scheduled(cron = "0 * * * * ?")
    private Flux<Alert> populateList() throws IOException {
        Map<String, String> result = cloudstackAlertService.listAlerts();
        List<Alert> alertList = new ArrayList<>();

        List<String> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String key : result.keySet()) {
            ListAlertsResponse listAlertsResponse = mapper.readValue(result.get(key), ListAlertsResponse.class);
            if (listAlertsResponse != null) {
                AlertResponse alertResponse = listAlertsResponse.getListalertsresponse();
                alertList = alertResponse.getAlert();
                allAlerts.addAll(alertList);
                alertList.forEach(alert -> {
                    resultList.add(key + " DESCRIPTION: " + alert.getDescription() + " NAME:" + alert.getName() +
                            " TIME: " + alert.getSent());
                });
            }
        }

        return Flux.fromIterable(allAlerts);
    }
}
