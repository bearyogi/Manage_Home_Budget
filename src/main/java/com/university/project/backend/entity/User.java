package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Person {

    private String username;
    private String passwordSalt;
    private String passwordHash;

    private void cos() {
        String name = firstName;
    }
}
