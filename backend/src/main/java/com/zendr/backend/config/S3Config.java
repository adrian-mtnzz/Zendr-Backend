package com.zendr.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    
    @Value("${application.storage.endpoint}")
    private String endpoint;
    
    @Value("${application.storage.region}")
    private String region;
    
    @Value("${application.storage.access-key}")
    private String accessKey;
    
    @Value("${application.storage.secret-key}")
    private String secretKey;
    
    @Bean
    public S3Client s3Client() {
        
        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(credentials)
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(false)
                                .build()
                )
                .build();
    }
}