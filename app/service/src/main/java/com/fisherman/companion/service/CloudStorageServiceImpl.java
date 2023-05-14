package com.fisherman.companion.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudStorageServiceImpl implements CloudStorageService {
    @Value("${gcp.cloud.bucket.name}")
    private String bucket;

    private final Storage storage;

    @Override
    public String uploadFile(final MultipartFile file) throws IOException {
        final String fileName = file.getOriginalFilename();
        final String randomString = UUID.randomUUID().toString();
        final String uniqueName = randomString + "_" + fileName;

        final byte[] fileContent = file.getBytes();

        final BlobId blobId = BlobId.of(bucket, uniqueName);

        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                                    .setContentType(file.getContentType())
                                    .build();

        final Blob blob = storage.create(blobInfo, fileContent);

        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://storage.googleapis.com/%s/%s", bucket, uniqueName);
    }
}
