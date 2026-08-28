package com.side.chubotamanju.global.config.cloudflare;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloudflare.r2")
public class R2Properties {
    private boolean enabled;
    private String accountId;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicBaseUrl;
    private String workerSignSecret;
    private long signedUrlTtlSeconds;
}
