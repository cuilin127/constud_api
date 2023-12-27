package com.pikachu.constdu.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class User {
    @Id //automatically assigned value
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//Primary Key

    //login method
    private String email;
    private String phoneNumber;
    private String password;

    private String firstName;
    private String lastName;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Case> cases = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    Role role = new Role();

    @OneToOne(fetch = FetchType.EAGER)
    private Passport passport;

    public String decodeUserPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(this.getPassword());
    }
}
