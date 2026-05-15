package com.zendr.backend.services.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {
    
    @Value("${application.storage.bucket}")
    private String bucket;
    
    @Value("${application.storage.domain}")
    private String domain;
    
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    
    public String uploadFile(MultipartFile file, String folder) {
        
        try {
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                throw new IllegalArgumentException("Solo se pueden subir imagenes");
            }
            
            String extension = Objects.requireNonNull(
                            file.getOriginalFilename())
                    .substring(file.getOriginalFilename()
                            .lastIndexOf("."));
            
            String fileName = folder + "/" + UUID.randomUUID() + extension;
            
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );
            
            return generatePresignedUrl(fileName);
            
        } catch (IOException e) {
            return null;
        }
    }
    
    public String generatePresignedUrl(String key) {
        
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
        
        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(30))
                        .getObjectRequest(getObjectRequest)
                        .build();
        
        PresignedGetObjectRequest presigned =
                s3Presigner.presignGetObject(presignRequest);
        
        return presigned.url().toString();
    }
}
