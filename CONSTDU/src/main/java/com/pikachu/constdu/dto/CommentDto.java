package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private String comment;

    private Long timestamp;

    private Long postId;

    private Integer posterId;

}
