package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Yuanfu Tian
 * Date: 2023-09-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;

    private String content;

    private String threadName;

    private Long timestamp;

    private UserDto poster;

    //public Post toPostWithoutId(){
    //    Post.builder().content(this.content).threadName(this.threadName).timestamp(this.timestamp).poster()
    //}

}
