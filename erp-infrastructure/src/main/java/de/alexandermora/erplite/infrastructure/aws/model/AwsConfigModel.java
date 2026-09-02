package de.alexandermora.erplite.infrastructure.aws.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "aws.s3")
public record AwsConfigModel(
        @NotBlank(message = "AWS S3 endpoint must not be blank")
        String endpoint,
        @NotBlank(message = "AWS S3 region must not be blank")
        String region,
        @NotBlank(message = "AWS S3 access key must not be blank")
        String accessKey,
        @NotBlank(message = "AWS S3 secret key must not be blank")
        String secretKey,
        @NotBlank(message = "AWS S3 bucket name must not be blank")
        String bucketName,
        boolean pathStyleEnabled
) {
    public String getBucketUrl() {
        return String.format("%s/%s", endpoint, bucketName);
    }
}
