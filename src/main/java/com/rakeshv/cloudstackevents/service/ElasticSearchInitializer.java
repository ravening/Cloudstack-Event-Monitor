package com.rakeshv.cloudstackevents.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.rakeshv.cloudstackevents.utils.Constants.INDEX_NAME;

@Component
@Slf4j
public class ElasticSearchInitializer {
    @Autowired
    ReactiveElasticsearchTemplate template;
    @Autowired
    ReactiveElasticsearchClient client;

    @EventListener
    public void createIndex(ApplicationReadyEvent event) {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(INDEX_NAME);

        client.indices()
                .existsIndex(request)
                .flatMap(indexExists -> {
                    log.info("Index {}: exists? {}", INDEX_NAME, indexExists);
                    if (!indexExists) {
                        log.error("Index doesnt exist");
                        return createIndex();
                    } else {
                        log.info("Index already exist");
                    }
                    return Mono.empty();
                }).block();
//        Mono<Boolean> exists = client.indices().existsIndex(request -> request.indices(INDEX_NAME));
//        exists.subscribe(ex -> {
//            if (!ex) {
//                log.info("Creating the index: {}", INDEX_NAME);
//                client.indices().createIndex(request -> request.index(INDEX_NAME));
//            } else {
//                log.info("Index {} already exists", INDEX_NAME);
//            }
//        });
    }

    private Mono<Void> createIndex() {
        CreateIndexRequest request = new CreateIndexRequest();
        request.index(INDEX_NAME);

        return client.indices()
                .createIndex(request)
                .doOnSuccess(aVoid -> log.info("Index created successfully"))
                .doOnError(throwable -> log.error("error: {}", throwable.getMessage()));
    }
}
