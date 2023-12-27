package com.pikachu.constdu.models;
import lombok.*;

import javax.persistence.*;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Case {
    @Id //automatically assigned value
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;//Primary Key
    //TODO:Information goes here

    private String caseName;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User customer;
}
