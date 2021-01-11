package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

    @Id
    @GeneratedValue
    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false; // null or other class
        }
        Person other = (Person) obj;

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }


}
