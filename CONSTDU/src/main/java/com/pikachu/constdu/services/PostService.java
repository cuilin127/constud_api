package com.pikachu.constdu.services;

import com.pikachu.constdu.models.Post;
import com.pikachu.constdu.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-20
 */
@Service
public class PostService {

     @Autowired
     private PostRepository repository;

     public Page<Post> findAll(int pageNumber, int pageSize){
         Pageable pageable = PageRequest.of(pageNumber, pageSize);
         return repository.findAll(pageable);
     }

    public Post savePost(Post post) {
        return repository.save(post);
    }

    public Post findPostById(Long id) {
        return repository.findPostById(id);
    }



}
