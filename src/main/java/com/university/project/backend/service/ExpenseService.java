package com.university.project.backend.service;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
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
    public List<Expense> findAllValue(Double data) {
        if(data == null || data.toString().isEmpty()){
            return expenseRepository.findAll();
        }
        return expenseRepository.findAllByValue(data);
    }

    public List<Expense> findAllName(String data) {
        if(data == null || data.isEmpty()){
            return expenseRepository.findAll();
        }
        return expenseRepository.findAllByName(data);
    }
    public List<Expense> findAllDate(LocalDate data) {
        if(data == null || data.toString().isEmpty()){
            return expenseRepository.findAll();
        }
        return expenseRepository.findAllByDate(data); }

    public List<Expense> findAllExpenseType(Object data) {

        if(data == null || data.toString().isEmpty()){
            return expenseRepository.findAll();
        }

        return expenseRepository.findAllByExpenseType(data); }
}
