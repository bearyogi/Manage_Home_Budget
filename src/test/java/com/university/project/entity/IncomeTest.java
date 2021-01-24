package com.university.project.entity;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Income;
import com.university.project.backend.entity.IncomeType;
import com.university.project.backend.entity.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;

@Test(groups = "Entity")
public class IncomeTest {
    private final Income emptyIncome = new Income();
    private final Income fullIncome = new Income();

    @Test
    public void shouldCreateEmptyBudget(){
        //Given
        Income testIncome = new Income();

        //When
        testIncome.setIncomeId(null);
        testIncome.setBudget(null);
        testIncome.setDate(null);
        testIncome.setIncomeType(null);
        testIncome.setName(null);
        testIncome.setValue(null);

        //Then
        assertEquals(testIncome.getIncomeId(), emptyIncome.getIncomeId(),"Should have null id, but have not.");
        assertEquals(testIncome.getBudget(), emptyIncome.getBudget(),"Should have null budget, but have not.");
        assertEquals(testIncome.getDate(), emptyIncome.getDate(),"Should have null date, but have not.");
        assertEquals(testIncome.getIncomeType(), emptyIncome.getIncomeType(),"Should have null income type, but have not.");
        assertEquals(testIncome.getName(), emptyIncome.getName(),"Should have null name, but have not.");
        assertEquals(testIncome.getValue(), emptyIncome.getValue(),"Should have null value, but have not.");
    }

    @Test
    public void shouldCreateFullBudget(){
        //Given
        Budget budget = new Budget();
        User user = new User();

        //When
        Income testExpense = new Income(1,"1",1.0, IncomeType.BONUS,LocalDate.of(1999,6,3),budget, user);

        //Then
        assertEquals(testExpense, fullIncome,"Should create full income, but did not.");
    }

    @BeforeTest
    public void fillExpense(){
        Budget budget = new Budget();
        fullIncome.setIncomeId(1);
        fullIncome.setBudget(budget);
        fullIncome.setDate(LocalDate.of(1999,6,3));
        fullIncome.setIncomeType(IncomeType.BONUS);
        fullIncome.setName("1");
        fullIncome.setValue(1.0);
    }
}
