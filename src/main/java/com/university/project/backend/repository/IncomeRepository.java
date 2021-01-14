package com.university.project.backend.repository;

import com.university.project.backend.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
    List<Income> findAllByValue(Double data);
    List<Income> findAllByName(String data);
    List<Income> findAllByDate(LocalDate data);
    List<Income> findAllByExpenseType(Object data);
}
