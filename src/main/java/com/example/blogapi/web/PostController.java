package com.example.blogapi.web;

import com.example.blogapi.domain.Post;
import com.example.blogapi.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> get() throws IOException {
        return ResponseEntity.ok(postService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Post post) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Post post) throws IOException {
        return ResponseEntity.ok(postService.update(id, post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(postService.show(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(postService.delete(id));
    }
}
