package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.Comment;
import com.pikachu.constdu.models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-21
 */
public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

}
