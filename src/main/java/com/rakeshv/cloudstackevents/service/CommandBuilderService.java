package com.rakeshv.cloudstackevents.service;

import com.rakeshv.cloudstackevents.models.CloudstackCommand;
import com.rakeshv.cloudstackevents.models.CloudstackHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandBuilderService {
    @Autowired
    private CloudstackApiService cloudstackApiService;
    @Autowired
    private Environment environment;

    private static final String USA = "usa";
    private static final String EUROPE = "europe";
    private static final String ASIA = "asia";

    public HashMap<String, CloudstackHandle> platformMap;
    List<Callable<String>> callableList = new ArrayList<>();
    static String[] platforms = new String[]{USA, EUROPE, ASIA};
    static final int NUMBER_OF_THREADS = platforms.length;

    @PostConstruct
    private void constructHandlers() {
        platformMap = new HashMap<>();

        for (String platform : platforms) {
            String url = environment.getProperty(platform + ".url");
            String apiKey = environment.getProperty(platform + ".apiKey");
            String secretKey = environment.getProperty(platform + ".secretKey");

            CloudstackHandle cloudstackHandle = CloudstackHandle.builder()
                    .url(url)
                    .apiKey(apiKey)
                    .secretKey(secretKey).build();

            platformMap.putIfAbsent(platform, cloudstackHandle);
        }
    }

    private String executeCommand(String command, HashMap<String, String> parameters, String platform) {
        CloudstackCommand cloudstackCommand = CloudstackCommand.builder()
                .command(command)
                .commandParameters(parameters).build();
        CloudstackHandle handle = platformMap.get(platform);
        return cloudstackApiService.executeCloudstackCommand(handle, cloudstackCommand);
    }

    public Map<String, String> executeCommand(String command, HashMap<String,String> parameters) {
        Map<String, String> resultMap = new HashMap<>();
        callableList = Arrays.asList(platforms)
                .stream()
                .parallel()
                .map(name -> {
                    Callable<String> callable = () -> {
                        String response = executeCommand(command, parameters, name);
                        if (response.contains("count")) {
                            resultMap.put(name, response);
                            return response;
                        }
                        return null;
                    };
                    return callable;
                })
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        try {
            List<Future<String>> futures = executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            log.error("Exception happened {}", e.getMessage());
        }

        return resultMap;
    }

}
