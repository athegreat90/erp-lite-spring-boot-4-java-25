package de.alexandermora.erplite.infrastructure.aws.config;


import de.alexandermora.erplite.infrastructure.aws.model.AwsConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class S3BucketConfig {

    @Bean
    public S3Client s3Client(AwsConfigModel awsConfig) {
       log.info("Configuring AWS S3 bucket");
        return S3Client.builder()
                .region(Region.of(awsConfig.region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsConfig.accessKey(), awsConfig.secretKey())
                ))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(awsConfig.pathStyleEnabled()).build())
                .endpointOverride(URI.create(awsConfig.endpoint()))
                .build();
    }
}
