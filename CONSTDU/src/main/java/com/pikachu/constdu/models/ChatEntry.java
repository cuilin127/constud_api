package com.pikachu.constdu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-03
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @Column(length = 5000)
    private String content;

    private Long timestamp;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = User.class)
    private User user;

}
