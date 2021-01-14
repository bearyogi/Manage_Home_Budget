package com.university.project.backend.service;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Expense;
import com.university.project.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService implements Dao<Expense> {
    @Autowired
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void delete(Expense expense) {
        expenseRepository.delete(expense);
    }

    @Override
    public Optional<Expense> get(Integer id) {
        return expenseRepository.findById(id);
    }

    @Override
    public List<Expense> getAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> getAllByBudget(Budget budget) {
        return expenseRepository.findAllByBudget(budget);
    }


    public List<Expense> getAllByValue(Budget budget, Double value) {
        if(value == null || value == 0.0){
            return expenseRepository.findAllByBudget(budget);
        }
        return expenseRepository.findAllByBudgetAndValue(budget, value);
    }

    public List<Expense> findAllName(Budget budget, String name) {
        if(name == null || name.isEmpty()){
            return expenseRepository.findAllByBudget(budget);
        }
        return expenseRepository.findAllByBudgetAndName(budget, name);
    }
    public List<Expense> findAllDate(Budget budget, LocalDate date) {
        if(date == null || date.toString().isEmpty()){
            return expenseRepository.findAllByBudget(budget);
        }
        return expenseRepository.findAllByBudgetAndDate(budget, date); }

    public List<Expense> findAllExpenseType(Budget budget, Object expenseType) {

        if(expenseType == null || expenseType.toString().isEmpty()){
            return expenseRepository.findAllByBudget(budget);
        }

        return expenseRepository.findAllByBudgetAndExpenseType(budget, expenseType); }
}
