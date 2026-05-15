package com.zendr.backend.services.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
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
    
    @Transactional
    public String uploadFile(MultipartFile file, String folder) {
        
        try {
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                throw new IllegalArgumentException("Solo se pueden subir imagenes");
            }
            
            String extension = Objects.requireNonNull(
                            file.getOriginalFilename())
                    .substring(file.getOriginalFilename()
                            .lastIndexOf("."));
            
            String fileName =
                    folder + "/" + UUID.randomUUID() + extension;
            
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );
            
            return buildFileUrl(fileName);
            
        } catch (IOException e) {
            return null;
        }
    }
    
    private String buildFileUrl(String fileName) {
        
        return "https://"+bucket+"."+domain+"/"+fileName;
    }
}
