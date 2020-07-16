package com.rakeshv.cloudstackevents.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import static com.rakeshv.cloudstackevents.utils.Constants.INDEX_NAME;

@Document(indexName = INDEX_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElasticsearchLog {
    @Id
    private String id;
    private String account;
    private String description;
    private String domain;
    private String domainid;
    private String uuid;
    private String level;
    private String state;
    private String type;
    private String username;
    private String name;
    private String platform;
    private String timestamp;
}
