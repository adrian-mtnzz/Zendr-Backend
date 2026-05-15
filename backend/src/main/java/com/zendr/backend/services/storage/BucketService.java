package com.zendr.backend.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface BucketService {
    public String uploadFile(MultipartFile file, String folder);
}
