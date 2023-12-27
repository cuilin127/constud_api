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
public class PaginationDto {

    private int page;

    private int pageSize;

}
