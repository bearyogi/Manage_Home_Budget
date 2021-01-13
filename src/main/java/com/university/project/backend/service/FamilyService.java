package com.university.project.backend.service;

import com.university.project.backend.entity.Family;
import com.university.project.backend.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyService implements Dao<Family> {
    @Autowired
    private final FamilyRepository familyRepository;

    public FamilyService(FamilyRepository familyRepository) {
        this.familyRepository = familyRepository;
    }

    @Override
    public Family save(Family family) {
        return familyRepository.save(family);
    }

    public Family update(Family family) {
        return familyRepository.save(family);
    }

    @Override
    public void delete(Family family) {
        familyRepository.delete(family);
    }

    @Override
    public Optional<Family> get(Integer id) {
        return familyRepository.findById(id);
    }

    @Override
    public List<Family> getAll() {
        return familyRepository.findAll();
    }

}
