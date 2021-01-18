package com.university.project.entity;

import com.university.project.backend.entity.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;

@Test(groups = "Entity")
public class FamilyTest {
    private Family emptyFamily = new Family();
    private Family fullFamily = new Family();

    @Test
    public void shouldCreateEmptyBudget(){
        //Given
        Family testFamily = new Family();

        //When
        testFamily.setFamilyId(null);
        testFamily.setBudget(null);
        testFamily.setFamilyName(null);

        //Then
        assertEquals(testFamily.getFamilyId(),emptyFamily.getFamilyId(),"Should have null id, but have not.");
        assertEquals(testFamily.getFamilyName(),emptyFamily.getFamilyName(),"Should have null name, but have not.");
        assertEquals(testFamily.getUsers(),emptyFamily.getUsers(),"Should have null user set, but have not.");
        assertEquals(testFamily.getBudget(),emptyFamily.getBudget(),"Should have null budget, but have not.");
    }

    @Test
    public void shouldCreateFullBudget(){
        //Given
        Set<User> users = new HashSet<>();
        Budget budget = new Budget();

        //When
        Family testFamily = new Family(1,"test",budget,users);

        //Then
        assertEquals(testFamily,fullFamily,"Should create full family, but did not.");
    }

    @BeforeTest
    public void fillFamily(){
        Budget budget = new Budget();
        Set<User> users = new HashSet<>();
        fullFamily.setFamilyId(1);
        fullFamily.setFamilyName("1");
        fullFamily.setBudget(budget);
        fullFamily.setUsers(users);
    }

}
