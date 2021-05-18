package com.geoway.utils;

import com.geoway.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/3 11:36
 * @Version 1.0
 */

public class ElasticConfig {
    public static void main(String[] args) throws IOException {
        ElasticConfig.HttpParseRequest("java").forEach(System.out::println);
    }

    public static List<Content> HttpParseRequest(String keyword) throws IOException {
        String url = "https://search.jd.com/Search?enc=utf-8&keyword=" + keyword;

        //声明httpGet请求对象
        HttpUtils httpUtils = new HttpUtils();
        String html = httpUtils.doGetHtml(url);
        Document document = Jsoup.parse(html);
        System.out.println(document.html());
        Element element = document.getElementById("J_goodsList");

        List<Content> contentList = new ArrayList<>();
        Elements elements = element.getElementsByTag("li");
        // Elements elements  =null;
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            System.out.println("============================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            contentList.add(content);
        }
        return contentList;

    }
}
