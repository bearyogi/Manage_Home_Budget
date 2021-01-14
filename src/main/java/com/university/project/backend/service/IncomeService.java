package com.university.project.backend.service;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.Income;
import com.university.project.backend.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class IncomeService implements Dao<Income> {
    @Autowired
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public Income save(Income income) {
        return incomeRepository.save(income);
    }

    public Income update(Income income) {
        return incomeRepository.save(income);
    }

    @Override
    public void delete(Income income) {
        incomeRepository.delete(income);
    }

    @Override
    public Optional<Income> get(Integer id) {
        return incomeRepository.findById(id);
    }

    @Override
    public List<Income> getAll() {
        return incomeRepository.findAll();
    }
    public List<Income> findAllValue(Double data) {
        if(data == null || data.toString().isEmpty()){
            return incomeRepository.findAll();
        }
        return incomeRepository.findAllByValue(data);
    }

    public List<Income> findAllName(String data) {
        if(data == null || data.isEmpty()){
            return incomeRepository.findAll();
        }
        return incomeRepository.findAllByName(data);
    }
    public List<Income> findAllDate(LocalDate data) {
        if(data == null || data.toString().isEmpty()){
            return incomeRepository.findAll();
        }
        return incomeRepository.findAllByDate(data); }

    public List<Income> findAllExpenseType(Object data) {

        if(data == null || data.toString().isEmpty()){
            return incomeRepository.findAll();
        }

        return incomeRepository.findAllByExpenseType(data); }

}