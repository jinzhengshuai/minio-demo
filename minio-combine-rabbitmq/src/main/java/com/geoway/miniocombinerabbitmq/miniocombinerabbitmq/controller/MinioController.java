package com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.controller;

import com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.utils.MinioUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/15 19:50
 * @Version 1.0
 */
@RestController
public class MinioController {

    @Resource
    private MinioUtils minioUtils;

    @Resource
    RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/upload")
    public void upload(@RequestParam("file") MultipartFile file) {
        minioUtils.upload(file);
        sendMessage("upload",file.getName());
    }

    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, @RequestParam(value = "fileName") String fileName) throws UnsupportedEncodingException {
        minioUtils.download(response, fileName);
        sendMessage("download",fileName);
    }

    @GetMapping(value = "/list")
    public List<MinioUtils.Album> list() {
        return minioUtils.list();
    }

    @GetMapping(value = "/objectName")
    public String getObject(@RequestParam(value = "fileName") String fileName) {
        return minioUtils.getObject(fileName);
    }

    @DeleteMapping(value = "/delete/{name}")
    public void delete(@PathVariable String name) {
        minioUtils.delete(name);
        sendMessage("delete",name);
    }

    public void sendMessage(String type,String message){
        String ExchangeName = "direct_order_exchange";
        String routerKey = "sms";
        rabbitTemplate.convertAndSend(ExchangeName,routerKey,type+":"+message);
    }


}
