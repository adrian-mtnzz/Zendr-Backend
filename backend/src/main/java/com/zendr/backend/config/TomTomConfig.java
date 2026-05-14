package com.zendr.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class TomTomConfig {
    
    @Bean
    public WebClient tomTomWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tomtom.com")
                .build();
    }
}
