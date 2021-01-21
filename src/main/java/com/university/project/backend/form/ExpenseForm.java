package com.university.project.backend.form;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.ExpenseType;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;

public class ExpenseForm extends FormLayout {

    public NumberField value = new NumberField("value");
    public TextField name = new TextField("name");
    public DatePicker date = new DatePicker("date");
    public ComboBox<ExpenseType> expenseType = new ComboBox<>("expenseType");

    public Button save = new Button("Zapisz");
    public Button delete = new Button("Usuń");
    public Button close = new Button("Anuluj");

    public Binder<Expense> binder = new Binder<>(Expense.class);
    private Expense expense;

    public ExpenseForm() {
        expenseType.setItems(ExpenseType.values());
        value.setValue(0.0);
        name.setValue("opis");
        date.setValue(LocalDate.now());
        expenseType.setValue(ExpenseType.FAMILY);

        name.setRequired(true);
        date.setRequired(true);
        addClassName("expense-form");

        binder.bindInstanceFields(this);
        add(
                value,
                name,
                expenseType,
                date,
                createButtonsLayout()
        );

        binder.forField(value).withValidator(value -> value != null && value.toString().length() > 0,"Kwota nie może być pusta!").bind(Expense::getValue, Expense::setValue);
        binder.forField(name).withValidator(name -> name != null && name.length() > 0,"Tytuł nie może być pusty!").bind(Expense::getName, Expense::setName);
        binder.forField(expenseType).withValidator(expenseType -> expenseType != null && !expenseType.name().equals(""),"Typ nie może być pusty!").bind(Expense::getExpenseType, Expense::setExpenseType);
        binder.forField(date).withValidator(date -> date != null && !date.toString().isEmpty(),"Data nie może być pusta!").bind(Expense::getDate, Expense::setDate);


    }

    public void setExpense(Expense expense) {
        this.expense = expense;
        binder.readBean(expense);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, expense)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void validateAndSave() {
        try {
            binder.writeBean(expense);
            fireEvent(new SaveEvent(this, expense));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class ExpenseFormEvent extends ComponentEvent<ExpenseForm> {
        private Expense expense;

        protected ExpenseFormEvent(ExpenseForm source, Expense expense) {
            super(source, false);
            this.expense = expense;
        }

        public Expense getExpense() {
            return expense;
        }
    }

    public static class SaveEvent extends ExpenseFormEvent {
        SaveEvent(ExpenseForm source, Expense expense) {
            super(source, expense);
        }
    }

    public static class DeleteEvent extends ExpenseFormEvent {
        DeleteEvent(ExpenseForm source, Expense expense) {
            super(source, expense);
        }

    }

    public static class CloseEvent extends ExpenseFormEvent {
        CloseEvent(ExpenseForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}