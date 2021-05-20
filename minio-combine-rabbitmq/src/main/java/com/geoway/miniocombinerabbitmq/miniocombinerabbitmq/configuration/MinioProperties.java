package com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/15 19:39
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * 对象存储服务的URL
     */
    private String endpoint;
    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;
    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 文件桶的名称
     */
    private String bucketName;


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

}

