package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

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

    @OneToOne(cascade = CascadeType.ALL)
    private Budget privateBudget;

    public User(String username, String plainPassword, String firstName, String lastName,
                String email, String phone) {
        this.username = username;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(plainPassword + passwordSalt);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone);
        this.privateBudget = new Budget();
    }

    public boolean checkPassword(String plainPassword) {
        return DigestUtils.sha1Hex(plainPassword + passwordSalt).equals(passwordHash);
    }

    @Override
    public String toString() {
        return getFirstName() +
                " " + getLastName();
    }
}
