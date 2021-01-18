package com.university.project.form;

import com.university.project.backend.entity.ExpenseType;
import com.university.project.backend.form.ExpenseForm;
import com.vaadin.flow.component.UI;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = ("forms"))
public class ExpenseFormTest {
    UI ui = new UI();
    @Test
    public void checkIfEmptyFormHaveInitialValues(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();

        //When

        //Then
        assertEquals(testExpenseForm.expenseType.getValue(), ExpenseType.FAMILY,"Initial expense type should be of family type, but is not");
        assertEquals(Optional.of(testExpenseForm.value.getValue()) , Optional.of(0.0),"Initial value should be 0.0, but is not");
        assertEquals(testExpenseForm.name.getValue(), "opis","Initial name should be opis, but is not");
        assertEquals(testExpenseForm.date.getValue(), LocalDate.now(),"Initial date should be localdate, but is not");
    }

    @Test
    public void checkIfInputsRequired(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();

        //When

        //Then
        assertTrue(testExpenseForm.name.isRequired(),"Should be required, but is not.");
        assertTrue(testExpenseForm.date.isRequired(),"Should be required, but is not.");
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void nameShouldNotBeNull(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();
        //When
        testExpenseForm.name.setValue(null);
        testExpenseForm.value.setValue(5.0);
        testExpenseForm.expenseType.setValue(ExpenseType.FAMILY);
        testExpenseForm.date.setValue(LocalDate.now());

    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void dateShouldNotBeNull(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();
        //When
        testExpenseForm.name.setValue("test");
        testExpenseForm.value.setValue(5.0);
        testExpenseForm.expenseType.setValue(ExpenseType.FAMILY);
        testExpenseForm.date.setValue(null);
        testExpenseForm.validateAndSave();
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void valueShouldNotBeNull(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();
        //When
        testExpenseForm.name.setValue("test");
        testExpenseForm.value.setValue(null);
        testExpenseForm.expenseType.setValue(ExpenseType.FAMILY);
        testExpenseForm.date.setValue(LocalDate.now());
        testExpenseForm.validateAndSave();
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void expenseTypeShouldNotBeNull(){
        //Given
        ExpenseForm testExpenseForm = new ExpenseForm();
        //When
        testExpenseForm.name.setValue("test");
        testExpenseForm.value.setValue(5.0);
        testExpenseForm.expenseType.setValue(null);
        testExpenseForm.date.setValue(LocalDate.now());
        testExpenseForm.validateAndSave();
    }

    @BeforeTest
    public void setEnviroment(){
        UI.setCurrent(ui);
    }
}
