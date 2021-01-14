package com.university.project.backend.repository;

import com.university.project.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findAllByValue(Double data);
    List<Expense> findAllByName(String data);
    List<Expense> findAllByDate(LocalDate data);
    List<Expense> findAllByExpenseType(Object data);

}
