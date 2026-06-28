package com.superflick.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "app.aws")
public class S3Config {

    private String region;
    private String bucket;
    private String accessKey;
    private String secretKey;
    /** Optional — set to http://localhost:4566 for LocalStack. */
    private String endpoint;

    @Bean
    public AmazonS3 amazonS3Client() {
        boolean hasCredentials = StringUtils.hasText(accessKey)
                && StringUtils.hasText(secretKey);

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();

        if (hasCredentials) {
            builder.withCredentials(new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(accessKey, secretKey)));
        } else {
            // Dev mode — no real AWS credentials configured.
            // The bean starts cleanly; any actual S3 call will fail with
            // a clear "Access Denied" rather than crashing at startup.
            log.warn("AWS credentials not set (app.aws.access-key / app.aws.secret-key). " +
                    "File uploads will fail at runtime. Set env vars for production.");
            builder.withCredentials(new AWSStaticCredentialsProvider(
                    new AnonymousAWSCredentials()));
        }

        if (StringUtils.hasText(endpoint)) {
            // LocalStack / MinIO support
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(
                            endpoint,
                            StringUtils.hasText(region) ? region : "us-east-1"));
            builder.withPathStyleAccessEnabled(true);
        } else {
            builder.withRegion(StringUtils.hasText(region) ? region : "ap-south-1");
        }

        return builder.build();
    }
}