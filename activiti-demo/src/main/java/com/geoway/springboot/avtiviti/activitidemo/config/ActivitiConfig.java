package com.geoway.springboot.avtiviti.activitidemo.config;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/24 19:17
 * @Version 1.0
 */
@Configuration
public class ActivitiConfig {

    @Bean
    public StandaloneProcessEngineConfiguration standaloneProcessEngineConfiguration(){
        return null;
    }
}
