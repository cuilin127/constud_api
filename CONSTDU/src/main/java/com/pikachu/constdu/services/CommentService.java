package com.pikachu.constdu.services;

import com.pikachu.constdu.models.Comment;
import com.pikachu.constdu.models.Post;
import com.pikachu.constdu.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-21
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAllByPost(Post post) {
        return commentRepository.findAllByPost(post);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }


}
