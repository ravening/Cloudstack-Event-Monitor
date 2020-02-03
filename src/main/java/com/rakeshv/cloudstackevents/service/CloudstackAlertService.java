package com.rakeshv.cloudstackevents.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.cloudstackevents.models.Alert;
import com.rakeshv.cloudstackevents.models.AlertResponse;
import com.rakeshv.cloudstackevents.models.ListAlertsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CloudstackAlertService {
    @Autowired
    private CommandBuilderService commandBuilderService;

    Set<Alert> alertsSet = new HashSet<>();
    boolean displayLog = false;
    @Scheduled(fixedRate = 60000)
    public void listAlerts() throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        List<Alert> alerts = new ArrayList<>();

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
                            log.error("PLATFORM: " + key + " " + alert);
                        }

                    }
                });
            }
        }

        displayLog = true;
    }
}
