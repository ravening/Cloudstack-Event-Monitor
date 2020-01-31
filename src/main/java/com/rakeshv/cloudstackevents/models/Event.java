package com.rakeshv.cloudstackevents.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String account;
    private String created;
    private String description;
    private String domain;
    private String domainid;
    private String id;
    private String level;
    private String state;
    private String type;
    private String username;
}
