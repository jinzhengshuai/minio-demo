package com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/13 20:42
 * @Version 1.0
 */
@Configuration
public class RabbitDirect {

    @Resource
    RabbitTemplate rabbitTemplate;


    @Bean
    public DirectExchange directExchange(){
        return  new DirectExchange("direct_order_exchange",true,false);
    }

    @Bean
    public Queue smsMessageDirect(){

        return new Queue("sms.direct.queue", true);
    }

    @Bean
    public Queue emilMessageDirect(){
        return  new Queue("emil.direct.queue",true);
    }


    @Bean
    public Binding smsBindingDirect(){
        return BindingBuilder.bind(smsMessageDirect()).to(directExchange()).with("sms");
    }

    @Bean
    public Binding emilBindingDirect(){
        return BindingBuilder.bind(emilMessageDirect()).to(directExchange()).with("emil");
    }





}
