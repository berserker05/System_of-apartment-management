package com.example.lease.common.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@ConfigurationProperties(prefix ="minio")
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(name ="minio.endpoint")
//@ConfigurationPropertiesScan("com.example.lease.common.minio")
public class MinioConfiguration {

   @Autowired
   private MinioProperties properties;

    @Bean
    public MinioClient getClient(){
        return MinioClient.builder().endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey()).build();
    }
}
