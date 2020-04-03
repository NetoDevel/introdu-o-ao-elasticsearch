package com.example.blogapi.service;

import com.example.blogapi.domain.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final String INDEX = "posts";
    private final String TYPE = "_doc";

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Post> findAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return convertToPost(searchResponse);
    }

    public List<Post> convertToPost(SearchResponse searchResponse) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Post> postDocuments = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            Post post = objectMapper.convertValue(hit.getSourceAsMap(), Post.class);
            post.setId(hit.getId());
            postDocuments.add(post);
        }

        return postDocuments;
    }

    public Post save(Post post) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, UUID.randomUUID().toString());
        indexRequest.source(objectMapper.writeValueAsString(post), XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
        return post;
    }

    public Post update(String id, Post post) throws IOException {
        //localhost:9200/posts/_doc/1/_update
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id);
        // body
        updateRequest.doc(objectMapper.writeValueAsString(post), XContentType.JSON);
        updateRequest.docAsUpsert(true);

        client.update(updateRequest, RequestOptions.DEFAULT).getId();

        return post;
    }

    public Post show(String id) throws IOException {
        //localhost:9200/posts/_doc/1
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.convertValue(getResponse.getSource(), Post.class);
    }

    public String delete(String id) throws IOException {
        //localhost:9200/posts/_doc/1 DELETE
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        return client.delete(deleteRequest, RequestOptions.DEFAULT).getResult().name();
    }
}
