package com.pikachu.constdu.dto;

import com.pikachu.constdu.models.ChatEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEntrySimpleDto {

    private String role;

    private String content;

    public static ChatEntrySimpleDto fromChatEntry(ChatEntry chatEntry) {
        return ChatEntrySimpleDto.builder()
                .role(chatEntry.getRole())
                .content(chatEntry.getContent())
                .build();
    }
}
