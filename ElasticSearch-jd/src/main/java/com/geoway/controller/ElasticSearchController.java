package com.geoway.controller;

import com.geoway.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/3 11:14
 * @Version 1.0
 */

/**
 * 在非模板页面的请求中，必循要用@RestController注解
 */
@RestController
public class ElasticSearchController {

    public final static String INDEX = "jd";

    @Resource
    private ContentService contentService;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/parse/{keyword}")
    public boolean getShopData(@PathVariable String keyword) throws IOException {
        //第一步判断索引是否存在
        boolean exist = contentService.indexIsExist(INDEX);
        if (exist) {
            exist = contentService.dataStorage(keyword);
        }
        return exist;
    }

    @GetMapping("/getData/{keyword}/{page}/{pageSize}")
    public List<Map<String, Object>> getJDData(@PathVariable("keyword") String keyword,
                                               @PathVariable("page") int page,
                                               @PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.getJDData(keyword, page, pageSize);
    }
}
