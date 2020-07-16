package com.rakeshv.cloudstackevents.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.ElasticsearchLog;
import com.rakeshv.cloudstackevents.models.Event;
import com.rakeshv.cloudstackevents.models.EventResponse;
import com.rakeshv.cloudstackevents.models.ListEventsResponse;
import com.rakeshv.cloudstackevents.repositories.ElasticsearchLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SsePushNotificationService {
    @Autowired
    CloudstackEventService cloudstackEventService;
    @Autowired
    ElasticsearchLogRepository elasticsearchLogRepository;

    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    ScheduledExecutorService scheduledExecutorService;
    ElasticsearchLog elasticsearchLog;

    public void addEmitter(final SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(final SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @EventListener
    public void createThreads(ApplicationReadyEvent event) {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
        elasticsearchLog = ElasticsearchLog.builder().build();
    }

    @Async
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
        List<Event> eventList;
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
                        elasticsearchLog.setAccount(event.getAccount());
                        elasticsearchLog.setDescription(event.getDescription());
                        elasticsearchLog.setDomain(event.getDomain());
                        elasticsearchLog.setDomainid(event.getDomainid());
                        elasticsearchLog.setUuid(event.getId());
                        elasticsearchLog.setLevel(event.getLevel());
                        elasticsearchLog.setState(event.getState());
                        elasticsearchLog.setType(event.getType());
                        elasticsearchLog.setUsername(event.getUsername());
                        elasticsearchLog.setPlatform(key);
                        elasticsearchLog.setTimestamp(event.getCreated());
                        elasticsearchLogRepository.save(elasticsearchLog).subscribe();
                        resultList.add(output);
                        log.error("{}", output);
                    }

                });
            }
        }

        return resultList;
    }

    Runnable runnable = () -> {
        try {
            populateEvents();
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
    };
}
