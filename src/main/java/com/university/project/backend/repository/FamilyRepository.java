package com.university.project.backend.repository;

import com.university.project.backend.entity.Family;
import com.university.project.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyRepository extends JpaRepository<Family, Integer> {

/*    @Query("SELECT family from Family family " +
            "INNER JOIN family.users user " +
            "WHERE user = ?1")*/
    List<Family> findAllByUsers(User user);
}
