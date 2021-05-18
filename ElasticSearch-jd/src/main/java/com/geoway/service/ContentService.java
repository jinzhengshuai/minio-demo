package com.geoway.service;

import com.alibaba.fastjson.JSON;
import com.geoway.controller.ElasticSearchController;
import com.geoway.pojo.Content;
import com.geoway.utils.ElasticConfig;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Jonathan Jin
 * @Date 2021/5/5 7:50
 * @Version 1.0
 */
@Service
public class ContentService {


    @Resource
    private RestHighLevelClient restHighLevelClient;


    public boolean indexIsExist(String index) throws IOException {
        GetIndexRequest getRequest = new GetIndexRequest(index);
        Boolean exists = restHighLevelClient.indices().exists(getRequest, RequestOptions.DEFAULT);
        if (exists == false) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("jd");
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        return true;
    }

    public boolean dataStorage(String keyword) throws IOException {
        List<Content> contentList = ElasticConfig.HttpParseRequest(keyword);
        System.out.println("contentList--" + contentList.size());
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1m");
        for (Content c : contentList) {
            bulkRequest.add(new IndexRequest(ElasticSearchController.INDEX).source(
                    JSON.toJSONString(c), XContentType.JSON
            ));
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    public List<Map<String, Object>> getJDData(String keyword, int page, int pageSize) {
        System.out.println("关键字---" + keyword);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest("jd");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //增加高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<span style ='color:red'>");
            //关闭多个高亮显示
            highlightBuilder.requireFieldMatch(false);
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
            //分页
            searchSourceBuilder.from(page);
            searchSourceBuilder.size(pageSize);
            //增加条件
            MatchPhraseQueryBuilder termQueryBuilder = QueryBuilders.matchPhraseQuery("title", keyword);
            searchSourceBuilder.query(termQueryBuilder);
            //设置过期时间
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit s : searchResponse.getHits()) {
                //取出原来的title
                Map<String, HighlightField> highlightField = s.getHighlightFields();
                HighlightField title = highlightField.get("title");
                Map<String, Object> sourceMap = s.getSourceAsMap();
                if (title != null) {
                    Text[] fragments = title.fragments();
                    String newTitle = "";
                    for (Text t : fragments) {
                        newTitle += t;
                    }
                    //替换成高亮的title
                    sourceMap.put("title", newTitle);
                }

                list.add(s.getSourceAsMap());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
