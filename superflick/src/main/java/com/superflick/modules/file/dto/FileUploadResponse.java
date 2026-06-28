package com.superflick.modules.file.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FileUploadResponse {
    /** Full public S3 URL of the uploaded file. */
    private String url;
    /** Original filename as uploaded by the client. */
    private String originalFilename;
    /** MIME content type of the file. */
    private String contentType;
    /** File size in bytes. */
    private long sizeBytes;
    /** S3 object key — useful for deletion or pre-signed URL generation. */
    private String s3Key;
}