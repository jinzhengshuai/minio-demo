package com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/15 19:42
 * @Version 1.0
 */
@Configuration
public class MinioConfig {
    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = null;
        try {
             minioClient =
                    MinioClient.builder()
                            .endpoint(properties.getEndpoint())
                            .credentials(properties.getAccessKey(), properties.getSecretKey())
                            .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return minioClient;
    }

}
