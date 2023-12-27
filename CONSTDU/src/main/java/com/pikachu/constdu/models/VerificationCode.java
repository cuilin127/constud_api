package com.pikachu.constdu.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class VerificationCode {
    @Id //automatically assigned value
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//Primary Key

    private String email;
    private String code;

    @Builder.Default
    private Date createTime = new Date();


}
