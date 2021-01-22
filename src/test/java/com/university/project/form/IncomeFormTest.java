package com.university.project.form;

import com.university.project.backend.entity.IncomeType;
import com.university.project.backend.form.IncomeForm;
import com.vaadin.flow.component.UI;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = ("forms"))
public class IncomeFormTest {
    UI ui = new UI();
    @Test
    public void checkIfEmptyFormHaveInitialValues(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();

        //When

        //Then
        assertEquals(testIncomeForm.incomeType.getValue(), IncomeType.BONUS,"Initial income type should be of family type, but is not");
        assertEquals(Optional.of(testIncomeForm.value.getValue()) , Optional.of(0.0),"Initial value should be 0.0, but is not");
        assertEquals(testIncomeForm.name.getValue(), "opis","Initial name should be opis, but is not");
        assertEquals(testIncomeForm.date.getValue(), LocalDate.now(),"Initial date should be localdate, but is not");
    }

    @Test
    public void checkIfInputsRequired(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();

        //When

        //Then
        assertTrue(testIncomeForm.name.isRequired(),"Should be required, but is not.");
        assertTrue(testIncomeForm.date.isRequired(),"Should be required, but is not.");
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void nameShouldNotBeNull(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();
        //When
        testIncomeForm.name.setValue(null);
        testIncomeForm.value.setValue(5.0);
        testIncomeForm.incomeType.setValue(IncomeType.BONUS);
        testIncomeForm.date.setValue(LocalDate.now());
        testIncomeForm.validateAndSave();
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void dateShouldNotBeNull(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();
        //When
        testIncomeForm.name.setValue("test");
        testIncomeForm.value.setValue(5.0);
        testIncomeForm.incomeType.setValue(IncomeType.BONUS);
        testIncomeForm.date.setValue(null);
        testIncomeForm.validateAndSave();
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void valueShouldNotBeNull(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();
        //When
        testIncomeForm.name.setValue("test");
        testIncomeForm.value.setValue(null);
        testIncomeForm.incomeType.setValue(IncomeType.BONUS);
        testIncomeForm.date.setValue(LocalDate.now());
        testIncomeForm.validateAndSave();
    }

    //Then
    @Test(expectedExceptions = {NullPointerException.class})
    public void expenseTypeShouldNotBeNull(){
        //Given
        IncomeForm testIncomeForm = new IncomeForm();
        //When
        testIncomeForm.name.setValue("test");
        testIncomeForm.value.setValue(5.0);
        testIncomeForm.incomeType.setValue(null);
        testIncomeForm.date.setValue(LocalDate.now());
        testIncomeForm.validateAndSave();
    }

    @BeforeTest
    public void setEnviroment(){
        UI.setCurrent(ui);
    }
}
