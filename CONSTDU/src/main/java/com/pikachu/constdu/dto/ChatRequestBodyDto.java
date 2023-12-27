package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestBodyDto {
    private List<ChatEntrySimpleDto> messages;
}
