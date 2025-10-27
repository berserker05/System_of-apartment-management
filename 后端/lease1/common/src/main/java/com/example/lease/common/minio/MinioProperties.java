package com.example.lease.common.minio;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="minio")
public class MinioProperties {

    public String getEndpoint() {
        return endpoint;
    }
    private String endpoint;

    public String getAccessKey() {
        return accessKey;
    }
    private String accessKey;

    public String getSecretKey() {
        return secretKey;
    }
    private String secretKey;

    public String getBucketName() {
        return bucketName;
    }

    private String bucketName;
}
