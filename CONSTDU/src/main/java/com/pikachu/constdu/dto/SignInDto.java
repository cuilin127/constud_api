package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignInDto {
    Authentication authentication;
    private String email;
    private String password;
}
