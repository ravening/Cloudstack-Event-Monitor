package com.rakeshv.cloudstackevents.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CloudstackEventService {
    @Autowired
    private CommandBuilderService commandBuilderService;

    public Map<String, String> listEvents() {
        HashMap<String, String> parameters = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        LocalDateTime last = LocalDateTime.now().minusHours(1).minusMinutes(1);
        parameters.putIfAbsent("listall", "true");
        parameters.putIfAbsent("level", "error");
        parameters.putIfAbsent("startdate", dtf.format(last));
        parameters.putIfAbsent("enddate", dtf.format(now));
//        parameters.putIfAbsent("type", "VM.START");
//        parameters.putIfAbsent("type", "VOLUME.RESIZE");
        return commandBuilderService.executeCommand("listEvents", parameters);
    }
}
