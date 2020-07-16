package com.rakeshv.cloudstackevents.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Alert;
import com.rakeshv.cloudstackevents.models.AlertResponse;
import com.rakeshv.cloudstackevents.models.ElasticsearchLog;
import com.rakeshv.cloudstackevents.models.ListAlertsResponse;
import com.rakeshv.cloudstackevents.repositories.ElasticsearchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudstackAlertService {
    private final CommandBuilderService commandBuilderService;
    private final ElasticsearchLogRepository elasticsearchLogRepository;

    Set<Alert> alertsSet = new HashSet<>();
    ScheduledExecutorService scheduledExecutorService;
    boolean displayLog = false;
    ElasticsearchLog elasticsearchLog;

    @EventListener
    public void createThreads(ApplicationReadyEvent event) {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
        elasticsearchLog = ElasticsearchLog.builder().build();
    }

    public void listAlerts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Alert> alerts;

        Map<String, String> resultMap = commandBuilderService.executeCommand("listAlerts", null);
        for (String key : resultMap.keySet()) {
            ListAlertsResponse listAlertsResponse = mapper.readValue(resultMap.get(key), ListAlertsResponse.class);
            if (listAlertsResponse != null) {
                AlertResponse alertResponse = listAlertsResponse.getListalertsresponse();
                alerts = alertResponse.getAlert();
                alerts.forEach(alert ->
                {
                    if (!alertsSet.contains(alert)) {
                        alertsSet.add(alert);
                        if (displayLog) {
                            elasticsearchLog.setDescription(alert.getDescription());
                            elasticsearchLog.setName(alert.getName());
                            elasticsearchLog.setTimestamp(alert.getSent());
                            elasticsearchLog.setPlatform(key);
                            elasticsearchLogRepository.save(elasticsearchLog).subscribe();
                            log.error("PLATFORM: " + key + " " + alert);
                        }

                    }
                });
            }
        }

        displayLog = true;
    }

    Runnable runnable = () -> {
        try {
            listAlerts();
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
    };
}
