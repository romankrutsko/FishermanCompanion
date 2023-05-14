package com.fisherman.companion.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageService {
    String uploadFile(MultipartFile file) throws IOException;
}