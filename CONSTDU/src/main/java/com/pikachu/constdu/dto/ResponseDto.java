package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {

    @Builder.Default
    public int status = 200;
    @Builder.Default
    public String message = "success";
    public T data;

    public ResponseDto(String message, T data){
        this.data = data;
        this.message = message;
    }
    public ResponseDto(String message){
        this.message = message;
        this.data = null;
    }
}

