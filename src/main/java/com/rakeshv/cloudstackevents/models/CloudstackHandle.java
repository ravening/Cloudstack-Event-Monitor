package com.rakeshv.cloudstackevents.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudstackHandle {
    private String apiKey;
    private String secretKey;
    private String url;
}
