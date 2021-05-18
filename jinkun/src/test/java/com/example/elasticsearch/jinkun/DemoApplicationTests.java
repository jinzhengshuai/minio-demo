package com.example.elasticsearch.jinkun;

import com.alibaba.fastjson.JSON;
import com.example.elasticsearch.jinkun.entity.User;
import lombok.val;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    void contextLoads() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("jinkun_index1");
        CreateIndexResponse response =
                restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(response);
    }

    @Test
    void getIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("jinkun_index");
        boolean response =
                restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        System.out.println(response);
    }

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("jinkun_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    @Test
    void getData() throws IOException {
        User user = new User("小金金", 23, "男");
        IndexRequest indexRequest = new IndexRequest("jinkun_index1");
        indexRequest.id("1");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //将数据放入JSON，进行添加
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
        System.out.println(indexResponse.status());


    }

    /**
     * 获取文档信息
     *
     * @throws IOException
     */
    @Test
    void getDocument() throws IOException {
        GetRequest getRequest = new GetRequest("jinkun_index1", "2");

        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);
    }


    /**
     * 修改文档信息
     *
     * @throws IOException
     */
    @Test
    void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("jinkun_index1", "2");
        updateRequest.timeout("1s");
        User user = new User("老金", 36, "男");
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse doc = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(doc.status());
    }

    /**
     * 删除文档信息
     *
     * @throws IOException
     */
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("jinkun_index1", "1");
        deleteRequest.timeout("1s");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }


    /**
     * 批量插入文档信息
     *
     * @throws IOException
     */
    @Test
    void bulkDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("5s");
        List<User> userList = new ArrayList<>();
        userList.add(new User("金焜", 26, "男"));
        userList.add(new User("金正帅", 26, "男"));
        userList.add(new User("金歪焜", 26, "男"));
        userList.add(new User("金邦政", 26, "男"));
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jinkun_index1")
                            .id(String.valueOf(i + 4))
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());
    }

    /**
     * 构建搜索
     */
    @Test
    void searchTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest("jinkun_index1");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // QueryBuilders.matchQuery 完全匹配
        // QueryBuilders.termQuery  包含关键字
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "焜");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(searchResponse.getHits());
        System.out.println("--------------------------------");
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsMap());
        }

    }

}
