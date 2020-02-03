package com.rakeshv.cloudstackevents.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Event;
import com.rakeshv.cloudstackevents.models.EventResponse;
import com.rakeshv.cloudstackevents.models.ListEventsResponse;
import com.rakeshv.cloudstackevents.service.CloudstackEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class WebSocketController {
    @Autowired
    private CloudstackEventService cloudstackEventService;
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public List<String> listEvents() throws IOException {
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
                    resultList.add(key + " DOMAIN:" + event.getDomain() + " " +
                            event.getCreated() + " DESCRIPTION:" +
                            event.getDescription() + " TYPE:" + event.getType());
                });
            }
        }

        return resultList;
    }

    @RequestMapping("/test")
    public String index(final Model model) {
        return "index";
    }
}
