package com.university.project.backend.service;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService implements Dao<Budget> {
    @Autowired
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }

    @Override
    public Optional<Budget> get(Integer id) {
        return budgetRepository.findById(id);
    }

    @Override
    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

}
