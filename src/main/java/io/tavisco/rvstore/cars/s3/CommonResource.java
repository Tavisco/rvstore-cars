package io.tavisco.rvstore.cars.s3;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import io.tavisco.rvstore.cars.dto.FormData;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommonResource {
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @ConfigProperty(name = "bucket.name")
    String bucketName;

    public PutObjectRequest buildPutRequest(final FormData formData, final String userUid) {
        String fileUuid = UUID.randomUUID().toString().substring(0, 8);
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(String.format("%s-%s-%s", userUid, fileUuid, formData.fileName))
                .contentType(formData.mimeType)
                .build();
    }

    public GetObjectRequest buildGetRequest(final String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    public File tempFilePath() {
        return new File(TEMP_DIR, new StringBuilder()
                .append("s3AsyncDownloadedTemp")
                .append((new Date()).getTime())
                .append(UUID.randomUUID())
                .append(".").append(".tmp")
                .toString());
    }

    public File uploadToTemp(final InputStream data) {
        File tempPath;
        try {
            tempPath = File.createTempFile("uploadS3Tmp", ".tmp");
            Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return tempPath;
    }

    public String getBucketName() {
        return bucketName;
    }
}
