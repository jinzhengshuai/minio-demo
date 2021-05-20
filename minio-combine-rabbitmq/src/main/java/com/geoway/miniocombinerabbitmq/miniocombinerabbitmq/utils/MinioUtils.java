package com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.utils;

import com.geoway.miniocombinerabbitmq.miniocombinerabbitmq.configuration.MinioProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.iherus.codegen.qrcode.QrcodeConfig;
import org.iherus.codegen.qrcode.SimpleQrcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author Jonathan Jin
 *
 *
 */
@Component
public class MinioUtils {

    Logger logger = LoggerFactory.getLogger(MinioUtils.class);

    @Resource
    private MinioProperties properties;

    @Resource
    private MinioClient minioClient;

    /**
     * 文件上传
     *
     * @param file file
     */
    public void upload(MultipartFile file) {
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            boolean isExist =  minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
            if (!isExist) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucketName())
                                .build());
            }
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(properties.getBucketName()).object(file.getOriginalFilename()).stream(
                            inputStream, -1, 10485760)
                            .contentType(file.getContentType())
                            .build());
            logger.info("上传到{}存储桶成功",properties.getBucketName());
            //关闭
            inputStream.close();
        } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    /**
     * 下载
     *
     * @param response response
     * @param fileName fileName
     */
    public void download(HttpServletResponse response, String fileName) {
        InputStream inputStream = null;
        try {
            ObjectStat stat  = minioClient.statObject(
                    StatObjectArgs.builder().bucket(properties.getBucketName()).object(fileName).build());
            inputStream =minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .build());
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
            IOUtils.copy(inputStream, response.getOutputStream());
            logger.info("从{}下载文件成功",properties.getBucketName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件url
     *
     * @param objectName objectName
     * @return url
     */
    public String getObject(String objectName) {
        try {
            return minioClient.getObjectUrl(properties.getBucketName(), objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取所有
     *
     * @return List<Album>
     */
    public List<Album> list() {
        try {
            List<Album> list = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(properties.getBucketName()).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                // Create a new Album Object
                Album album = new Album();
                // Set the presigned URL in the album object
                album.setUrl(minioClient.getObjectUrl(properties.getBucketName(), item.objectName()));
                album.setDescription(item.objectName() + "," + item.lastModified() + ",size:" + item.size());
                // Add the album object to the list holding Album objects
                list.add(album);
            }
            logger.info("文件列表",list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件移除
     *
     * @param name 文件名
     */
    public void delete(String name) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(properties.getBucketName()).object(name).build());
            logger.info("删除{}了文件",name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传生成的二维码
     */
    public void generator() {

        String uuid = UUID.randomUUID().toString();
        InputStream inputStream = bufferedImageToInputStream(qrcode(uuid));
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(properties.getBucketName()).object(uuid + ".png").stream(
                            bufferedImageToInputStream(qrcode(uuid)), -1, inputStream.available())
                            .contentType(MediaType.IMAGE_PNG_VALUE)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BufferedImage qrcode(String content) {
        QrcodeConfig config = new QrcodeConfig()
                .setBorderSize(2)
                .setPadding(12)
                .setMasterColor("#00BFFF")
                .setLogoBorderColor("#B0C4DE")
                .setHeight(250).setWidth(250);
        return new SimpleQrcodeGenerator(config).setLogo("src/main/resources/logo.png").generate(content).getImage();
    }

    /**
     * @param image image
     * @return InputStream
     */
    public InputStream bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class Album {
        private String url;
        private String description;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;

        }
    }

}
