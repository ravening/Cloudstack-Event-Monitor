package com.rakeshv.cloudstackevents.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Event;
import com.rakeshv.cloudstackevents.models.EventResponse;
import com.rakeshv.cloudstackevents.models.ListEventsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SsePushNotificationService {
    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Autowired
    CloudstackEventService cloudstackEventService;
    public void addEmitter(final SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(final SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void doNotify() throws IOException {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                List<String> eventList = populateEvents();
                if (eventList != null && eventList.size() > 0) {
                        emitter.send(SseEmitter.event()
                                .data(eventList));
                }
//                        .data(DATE_FORMATTER.format(new Date()) + " : " + UUID.randomUUID().toString()));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    public List<String> populateEvents() throws IOException {
        Map<String, String> result = cloudstackEventService.listEvents();
        List<Event> eventList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String key : result.keySet()) {
            ListEventsResponse listEventsResponse = mapper.readValue(result.get(key), ListEventsResponse.class);
            if (listEventsResponse != null) {
                EventResponse eventResponse = listEventsResponse.getListeventsresponse();
                eventList = eventResponse.getEvent();
                eventList.forEach(event -> {
                    if (!event.getType().contains("SNAPSHOT.CREATE")) {
                        String output = "PLATFORM: " + key + " " + event.toString();
                        resultList.add(output);
                        log.error("{}", output);
                    }

                });
            }
        }

        return resultList;
    }
}
