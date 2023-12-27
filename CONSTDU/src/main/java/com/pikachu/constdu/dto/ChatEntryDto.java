package com.pikachu.constdu.dto;

import com.pikachu.constdu.models.ChatEntry;
import com.pikachu.constdu.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEntryDto {

    private Long id;

    private String role;

    private String content;

    private Long timestamp;

    private Integer userId;


    public static ChatEntryDto fromChatEntry(ChatEntry chatEntry) {
        return ChatEntryDto.builder()
                .id(chatEntry.getId())
                .role(chatEntry.getRole())
                .content(chatEntry.getContent())
                .timestamp(chatEntry.getTimestamp())
                .userId(chatEntry.getUser().getId())
                .build();
    }

    public static ChatEntry generateChatEntryByChatEntryDto(ChatEntryDto chatEntryDto, User user) {
        return ChatEntry.builder()
                .role(chatEntryDto.getRole())
                .content(chatEntryDto.getContent())
                .timestamp(Instant.now().toEpochMilli())
                .user(user)
                .build();
    }

}
