package com.rakeshv.cloudstackevents.utils;

import com.rakeshv.cloudstackevents.models.ElasticsearchLog;
import com.rakeshv.cloudstackevents.repositories.ElasticsearchLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DbSeeder {
    @Autowired
    ElasticsearchLogRepository elasticsearchLogRepository;
    @EventListener
    public void storeEvent(ApplicationReadyEvent event) {
        ElasticsearchLog elasticsearchLog = ElasticsearchLog.builder()
                .account("test")
                .description("testing logs")
                .domain("root")
                .domainid("1234")
                .timestamp("2020-07-17T07:55:16+0000")
                .platform("earth")
                .build();

//        elasticsearchLogRepository.save(elasticsearchLog)
//                .subscribe(entry -> log.info("saved"),
//                        error -> log.error("error: {}", error.getMessage()),
//                        () -> log.info("completed"));
    }
}
