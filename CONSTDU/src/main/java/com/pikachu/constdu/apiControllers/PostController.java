package com.pikachu.constdu.apiControllers;

import com.pikachu.constdu.dto.PaginationDto;
import com.pikachu.constdu.dto.PostDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.dto.UserDto;
import com.pikachu.constdu.models.Post;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.services.PostService;
import com.pikachu.constdu.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-17
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/posts")
    public ResponseEntity<ResponseDto<Page<PostDto>>> getPostsByPagination(@RequestBody PaginationDto paginationDto) {
        Page<Post> entityPage = postService.findAll(paginationDto.getPage(), paginationDto.getPageSize());
        Page<PostDto> unsortedPage = entityPage.map(post -> PostDto.builder()
                .id(post.getId())
                .threadName(post.getThreadName())
                .content(post.getContent())
                .timestamp(post.getTimestamp())
                .poster(UserDto.builder()
                        .id(post.getPoster().getId())
                        .firstName(post.getPoster().getFirstName())
                        .lastName(post.getPoster().getLastName())
                        .phoneNumber(post.getPoster().getPhoneNumber())
                        .email(post.getPoster().getEmail())
                        .role(post.getPoster().getRole().getRoleName())
                        .build()
                )
                .build());

        List<PostDto> sortedList = unsortedPage.getContent().stream().sorted((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp())).collect(Collectors.toList());
        Page<PostDto> res = new PageImpl<>(sortedList, entityPage.getPageable(), entityPage.getTotalElements());

        return ResponseEntity.ok(ResponseDto.<Page<PostDto>>builder().data(res).build());
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseDto<PostDto>> addPost(@RequestBody PostDto postDto) {
        User u = userService.getUserByToken();

        Post post = Post.builder()
                .threadName(postDto.getThreadName())
                .content(postDto.getContent())
                .timestamp(Instant.now().toEpochMilli())
                .poster(u)
                .build();
        PostDto res = null;

        try {
            Post savedPost = postService.savePost(post);
            res = PostDto.builder()
                    .id(savedPost.getId())
                    .threadName(savedPost.getThreadName())
                    .content(savedPost.getContent())
                    .timestamp(savedPost.getTimestamp())
                    .poster(UserDto.builder()
                            .id(u.getId())
                            .firstName(u.getFirstName())
                            .lastName(u.getLastName())
                            .email(u.getEmail())
                            .phoneNumber(u.getPhoneNumber())
                            .role(u.getRole().getRoleName())
                            .build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ResponseDto.<PostDto>builder().status(500).message("Server Internal Error").data(res).build());
        }
        return ResponseEntity.ok(ResponseDto.<PostDto>builder().data(res).build());
    }


}
