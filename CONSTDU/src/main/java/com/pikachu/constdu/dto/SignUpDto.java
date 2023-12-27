package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpDto {
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
}
