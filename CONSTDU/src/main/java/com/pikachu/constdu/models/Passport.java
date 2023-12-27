package com.pikachu.constdu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Passport {
    @Id //automatically assigned value
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//Primary Key

    private String name;
    private String surname;
    private String sex;
    private String dateOfBirth;
    private String nationality;
    private String passportType;
    private String passportNumber;
    private String issuingCountry;
    private String expirationDate;
    private String personalNumber;

    @OneToOne
    @JoinColumn(name="user_id", nullable=false)
    private User customer;
}
