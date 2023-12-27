package com.pikachu.constdu.apiControllers;

import com.pikachu.constdu.dto.CommentDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.models.Comment;
import com.pikachu.constdu.models.Post;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.services.CommentService;
import com.pikachu.constdu.services.PostService;
import com.pikachu.constdu.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-21
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/comments/{postId}")
    public ResponseEntity<ResponseDto<List<CommentDto>>> getAllCommentsByPostId(@PathVariable Long postId) {
        // get post instance
        Post post = postService.findPostById(postId);
        // get all the comment by passing in post
        List<Comment> commentList = commentService.findAllByPost(post);

        // Sort comments in descending order by timestamp
        List<Comment> sortedComments = commentList.stream().sorted(Comparator.comparing(Comment::getTimestamp).reversed()).toList();

        // Convert Comment list to CommentDto list
        List<CommentDto> res = sortedComments.stream().map(comment ->
            CommentDto.builder()
                    .id(comment.getId())
                    .comment(comment.getComment())
                    .timestamp(comment.getTimestamp())
                    .postId(comment.getPost().getId())
                    .posterId(comment.getPoster().getId())
                    .build()
        ).toList();

        return ResponseEntity.ok(ResponseDto.<List<CommentDto>>builder().data(res).build());
    }

    @PostMapping("/addComment")
    public ResponseEntity<ResponseDto<CommentDto>> addCommentByPostId(@RequestBody CommentDto commentDto) {
        User user = userService.getUserByToken();
        Post post = postService.findPostById(commentDto.getPostId());

        Comment comment = Comment.builder()
                .comment(commentDto.getComment())
                .timestamp(Instant.now().toEpochMilli())
                .post(post)
                .poster(user)
                .build();

        Comment savedComment = commentService.saveComment(comment);
        CommentDto res = CommentDto.builder()
                .id(savedComment.getId())
                .comment(savedComment.getComment())
                .timestamp(savedComment.getTimestamp())
                .postId(savedComment.getPost().getId())
                .posterId(savedComment.getPoster().getId())
                .build();

        return ResponseEntity.ok(ResponseDto.<CommentDto>builder().data(res).build());
    }


}
