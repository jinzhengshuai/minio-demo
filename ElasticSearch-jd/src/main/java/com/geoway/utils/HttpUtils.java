package com.geoway.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class HttpUtils {

    private PoolingHttpClientConnectionManager cm;

    public HttpUtils() {
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        cm.setMaxTotal(200);
        //设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);
    }

    /**
     * 根据请求地址下载页面数据
     *
     * @param url
     * @return 页面数据
     */
    public String doGetHtml(String url) {
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());

        //设置请求Request Headers中的User-Agent，告诉京东说这是浏览器访问
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36");

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，返回response
            response = httpClient.execute(httpGet);
            //解析response返回数据
            if (response.getStatusLine().getStatusCode() == 200) {
                String html = "";
                //如果response.getEntity获取的结果为空，在执行EntityUtils.toString会报错
                //需要对Entity进行非空的判断
                if (response.getEntity() != null) {
                    html = EntityUtils.toString(response.getEntity(), "utf8");
                }
                return html;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载失败，返回空串
        return "";
    }

    /**
     * 下载图片
     *
     * @param url
     * @return 图片名称
     */
    public String doGetImage(String url) {
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，返回response
            response = httpClient.execute(httpGet);
            //解析response,返回结果
            if (response.getStatusLine().getStatusCode() == 200) {
                //图片后缀
                String extName = url.substring(url.lastIndexOf("."));
                //重命名
                String imageName = UUID.randomUUID().toString() + extName;
                //声明输出的文件
                FileOutputStream outputStream = new FileOutputStream(new File("D:\\images\\" + imageName));
                //下载图片
                response.getEntity().writeTo(outputStream);
                //返回生成的图片名
                return imageName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载失败，返回空串
        return "";
    }

    //获取请求参数对象
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000)  //创建连接的最长时间
                .setConnectionRequestTimeout(500) //获取连接的最长时间
                .setSocketTimeout(100000)  //数据传输的最长时间
                .build();
        return config;
    }
}
