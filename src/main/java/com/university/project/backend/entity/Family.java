package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @OneToOne(cascade = CascadeType.ALL)
    private Budget budget;

    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinTable(name = "user_family",
            joinColumns = @JoinColumn(name = "family_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> users;

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
}
