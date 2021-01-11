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
public abstract class ItemClass {

    @Id
    @GeneratedValue
    private Integer id;

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemClass)) {
            return false; // null or other class
        }
        ItemClass other = (ItemClass) obj;

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }
}