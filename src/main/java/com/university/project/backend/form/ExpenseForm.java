package com.university.project.backend.form;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.ExpenseType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
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

public class ExpenseForm extends FormLayout {

    NumberField kwota = new NumberField("kwota");
    TextField tytul = new TextField("nazwisko");
    DatePicker data = new DatePicker("data");
    ComboBox<ExpenseType> typ = new ComboBox<>("typ");

    Button save = new Button("Zapisz");
    Button delete = new Button("Usuń");
    Button close = new Button("Anuluj");

    Binder<Expense> binder = new Binder<>(Expense.class);
    private Expense expense;

    public ExpenseForm() {
        tytul.setRequired(true);
        data.setRequired(true);
        addClassName("expense-form");

        binder.bindInstanceFields(this);
        add(
                kwota,
                tytul,
                typ,
                data,
                createButtonsLayout()
        );
        typ.setItems(ExpenseType.values());
        binder.forField(typ).withValidator(typ -> !typ.equals(""),"Typ nie może być pusty!").bind(Expense::getExpenseType, Expense::setExpenseType);
        binder.forField(kwota).withValidator(kwota -> kwota.toString().length() > 0,"Kwota nie może być pusta!").bind(Expense::getValue, Expense::setValue);
        binder.forField(tytul).withValidator(tytul -> tytul.length() > 0,"Tytuł nie może być puste!").bind(Expense::getName, Expense::setName);
        binder.forField(data).withValidator(data -> !data.equals(""),"Data nie może być pusta!").bind(Expense::getDate, Expense::setDate);


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

    private void validateAndSave() {

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
