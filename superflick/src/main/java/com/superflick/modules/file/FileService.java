package com.superflick.modules.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.superflick.config.S3Config;
import com.superflick.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3  s3Client;
    private final S3Config  s3Config;

    /**
     * Uploads a file to S3 and returns the public URL.
     * If AWS credentials are not configured (dev mode), logs a warning
     * and returns a placeholder URL instead of throwing.
     */
    public String uploadFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty())
            throw new BadRequestException("File is empty");

        // Dev guard — skip real S3 call if bucket not configured
        if (!StringUtils.hasText(s3Config.getBucket())
                || "superflick-dev".equals(s3Config.getBucket())
                && !StringUtils.hasText(s3Config.getAccessKey())) {
            String placeholder = "https://placeholder.superflick.dev/"
                    + prefix + file.getOriginalFilename();
            log.warn("S3 not configured — returning placeholder URL: {}", placeholder);
            return placeholder;
        }

        String safeName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_")
                : "file";
        String key = prefix + UUID.randomUUID() + "_" + safeName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(s3Config.getBucket(), key, file.getInputStream(), metadata);
            String url = s3Client.getUrl(s3Config.getBucket(), key).toString();
            log.info("File uploaded: key={}", key);
            return url;
        } catch (IOException ex) {
            throw new BadRequestException("File upload failed: " + ex.getMessage());
        }
    }

    public void deleteFile(String key) {
        if (!StringUtils.hasText(s3Config.getBucket())) return;
        try {
            s3Client.deleteObject(s3Config.getBucket(), key);
        } catch (Exception ex) {
            log.warn("S3 delete failed for key={}: {}", key, ex.getMessage());
        }
    }
}