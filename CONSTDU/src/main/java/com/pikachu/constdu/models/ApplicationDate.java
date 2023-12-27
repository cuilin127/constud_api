package com.pikachu.constdu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by Yuanfu Tian
 * Date: 2023-11-14
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ApplicationDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_date")
    private Instant applicationDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
