package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-20
 */
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Post findPostById(Long id);

}
