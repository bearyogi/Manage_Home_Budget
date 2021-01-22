package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Family {
    @Id
    @GeneratedValue
    private Integer familyId;

    private String familyName;

    @OneToOne(cascade = CascadeType.ALL)
    private Budget budget;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_family",
            joinColumns = @JoinColumn(name = "family_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> users = new HashSet<>();

    @Override
    public int hashCode() {
        if (familyId != null) {
            return familyId.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Family)) {
            return false; // null or other class
        }
        Family other = (Family) obj;

        if (familyId != null) {
            return familyId.equals(other.familyId);
        }
        return super.equals(other);
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public boolean isUserInFamily(User user) {
        return users.contains(user);
    }
}
