package com.side.chubotamanju.global.config.cloudflare;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
@RequiredArgsConstructor
public class R2Config {

    private final R2Properties r2Properties;

    @Bean
    @ConditionalOnProperty(prefix = "cloudflare.r2", name = "enabled", havingValue = "true")
    public S3Client r2S3Client() {
        String endpoint = "https://%s.r2.cloudflarestorage.com".formatted(r2Properties.getAccountId());

        return S3Client.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .checksumValidationEnabled(false)
                        .build()
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(r2Properties.getAccessKey(), r2Properties.getSecretKey())
                        )
                )
                .build();
    }

    /**
     * Builds and configures the S3 presigner with R2-specific settings
     */
    @Bean
    @ConditionalOnProperty(prefix = "cloudflare.r2", name = "enabled", havingValue = "true")
    public S3Presigner r2S3Presigner() {
        String endpoint = "https://%s.r2.cloudflarestorage.com".formatted(r2Properties.getAccountId());

        return S3Presigner.builder()
                .region(Region.of("auto")) // Required by SDK but not used by R2
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(r2Properties.getAccessKey(), r2Properties.getSecretKey())
                        )
                )
                .build();
    }
}
