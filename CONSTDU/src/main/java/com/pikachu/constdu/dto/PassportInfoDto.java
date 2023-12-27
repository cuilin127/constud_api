package com.pikachu.constdu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportInfoDto {
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
}
