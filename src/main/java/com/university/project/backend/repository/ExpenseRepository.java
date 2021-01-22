package com.university.project.backend.repository;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findAllByBudget(Budget budget);

    List<Expense> findAllByBudgetAndValue(Budget budget, Double value);

    List<Expense> findAllByBudgetAndName(Budget budget, String name);

    List<Expense> findAllByBudgetAndDate(Budget budget, LocalDate date);

    List<Expense> findAllByBudgetAndExpenseType(Budget budget, Object expenseType);
}
