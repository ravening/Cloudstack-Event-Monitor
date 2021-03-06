package com.rakeshv.cloudstackevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableScheduling
@CrossOrigin(origins = "*")
@EnableReactiveElasticsearchRepositories
public class CloudstackEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudstackEventsApplication.class, args);
	}

}
