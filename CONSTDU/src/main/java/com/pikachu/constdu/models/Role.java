package com.pikachu.constdu.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    List<User> users = new ArrayList<User>();
}
