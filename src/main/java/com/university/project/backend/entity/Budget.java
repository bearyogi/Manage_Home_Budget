package com.university.project.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget{

    @Id
    @GeneratedValue
    private Integer budgetId;

    @OneToMany(mappedBy = "budget")
    private Set<Expense> expenses;

    @OneToMany(mappedBy = "budget")
    private Set<Income> incomes;

    @Override
    public int hashCode() {
        if (budgetId != null) {
            return budgetId.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Budget)) {
            return false; // null or other class
        }
        Budget other = (Budget) obj;

        if (budgetId != null) {
            return budgetId.equals(other.budgetId);
        }
        return super.equals(other);
    }
}
