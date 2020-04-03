package com.example.blogapi.domain;

import lombok.Data;

import java.util.List;

@Data
public class Post {

    private String id;
    private String title;
    private List<Tag> tags;
    private String author;
}
