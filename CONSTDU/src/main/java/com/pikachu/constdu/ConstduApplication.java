package com.pikachu.constdu;

import com.pikachu.constdu.configs.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class ConstduApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstduApplication.class, args);
    }

}
