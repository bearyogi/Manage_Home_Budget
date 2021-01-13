package com.university.project.backend.repository;

import com.university.project.backend.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
}
