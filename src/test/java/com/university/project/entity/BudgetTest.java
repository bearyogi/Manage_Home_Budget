package com.university.project.entity;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.Income;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
@Test(groups = "Entity")
public class BudgetTest {
private final Budget emptyBudget = new Budget();
private final Budget fullBudget = new Budget();

        @Test
        public void shouldCreateEmptyBudget(){
            //Given
            Budget testBudget = new Budget();

            //When
            testBudget.setBudgetId(null);
            testBudget.setExpenses(null);
            testBudget.setIncomes(null);

            //Then
            assertEquals(testBudget.getBudgetId(),emptyBudget.getBudgetId(),"Should have null id, but have not.");
            assertEquals(testBudget.getExpenses(),emptyBudget.getExpenses(),"Should have null expenses, but have not.");
            assertEquals(testBudget.getIncomes(),emptyBudget.getIncomes(),"Should have null incomes, but have not.");
        }

        @Test
        public void shouldCreateFullBudget(){
            //Given
            Set<Expense> setExpenses = new HashSet<>();
            Set<Income> setIncomes = new HashSet<>();

            //When
            Budget testBudget = new Budget(1,setExpenses,setIncomes);

            //Then
            assertEquals(testBudget,fullBudget,"Should create full budget, but did not.");
        }

        @BeforeTest
        public void fillBudget(){
        Set<Expense> setExpenses = new HashSet<>();
        Set<Income> setIncomes = new HashSet<>();
        fullBudget.setBudgetId(1);
        fullBudget.setExpenses(setExpenses);
        fullBudget.setIncomes(setIncomes);
        }

}
