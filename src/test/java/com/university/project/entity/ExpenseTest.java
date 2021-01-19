package com.university.project.entity;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.ExpenseType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.time.LocalDate;

import static org.testng.Assert.assertEquals;

@Test(groups = "Entity")
public class ExpenseTest {
    private Expense emptyExpense = new Expense();
    private Expense fullExpense = new Expense();

    @Test
    public void shouldCreateEmptyBudget(){
        //Given
        Expense testExpense = new Expense();

        //When
        testExpense.setExpenseId(null);
        testExpense.setBudget(null);
        testExpense.setDate(null);
        testExpense.setExpenseType(null);
        testExpense.setName(null);
        testExpense.setValue(null);

        //Then
        assertEquals(testExpense.getExpenseId(),emptyExpense.getExpenseId(),"Should have null id, but have not.");
        assertEquals(testExpense.getBudget(),emptyExpense.getBudget(),"Should have null budget, but have not.");
        assertEquals(testExpense.getDate(),emptyExpense.getDate(),"Should have null date, but have not.");
        assertEquals(testExpense.getExpenseType(),emptyExpense.getExpenseType(),"Should have null expense type, but have not.");
        assertEquals(testExpense.getName(),emptyExpense.getName(),"Should have null name, but have not.");
        assertEquals(testExpense.getValue(),emptyExpense.getValue(),"Should have null value, but have not.");
    }

    @Test
    public void shouldCreateFullBudget(){
        //Given
        Budget budget = new Budget();

        //When
        Expense testExpense = new Expense(1,"1",Double.valueOf(1),ExpenseType.EDUCATION,LocalDate.of(1999,06,03),budget);

        //Then
        assertEquals(testExpense,fullExpense,"Should create full expense, but did not.");
    }

    @BeforeTest
    public void fillExpense(){
        Budget budget = new Budget();
        fullExpense.setExpenseId(1);
        fullExpense.setBudget(budget);
        fullExpense.setDate(LocalDate.of(1999,06,03));
        fullExpense.setExpenseType(ExpenseType.EDUCATION);
        fullExpense.setName("1");
        fullExpense.setValue(Double.valueOf(1));
    }
}
