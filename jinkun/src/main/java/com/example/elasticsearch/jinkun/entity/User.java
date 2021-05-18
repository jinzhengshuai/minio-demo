package com.example.elasticsearch.jinkun.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * @Author Jonathan Jin
 * @Date 2021/5/1 12:07
 * @Version 1.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Component
@Data
public class User {
    private String name;
    private int age;
    private String gender;


}
