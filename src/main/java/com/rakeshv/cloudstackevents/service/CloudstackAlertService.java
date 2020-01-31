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
public class CloudstackAlertService {
    @Autowired
    private CommandBuilderService commandBuilderService;

    public Map<String, String> listAlerts() {
        HashMap<String, String> parameters = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last = LocalDateTime.now().minusMinutes(5);

        return commandBuilderService.executeCommand("listAlerts", null);
    }
}
