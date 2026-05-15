package com.zendr.backend.services.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BucketService {
    public String uploadFile(MultipartFile file, String folder) throws IOException;
}
