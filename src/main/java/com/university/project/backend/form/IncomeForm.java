package com.university.project.backend.form;

import com.university.project.backend.entity.Income;
import com.university.project.backend.entity.IncomeType;
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

import java.time.LocalDate;

public class IncomeForm extends FormLayout {

    NumberField value = new NumberField("value");
    TextField name = new TextField("name");
    DatePicker date = new DatePicker("date");
    ComboBox<IncomeType> incomeType = new ComboBox<>("incomeType");

    Button save = new Button("Zapisz");
    Button delete = new Button("Usuń");
    Button close = new Button("Anuluj");

    Binder<Income> binder = new Binder<>(Income.class);
    private Income income;

    public IncomeForm() {
        incomeType.setItems(IncomeType.values());
        value.setValue(0.0);
        name.setValue("opis");
        date.setValue(LocalDate.now());
        incomeType.setValue(IncomeType.BONUS);

        name.setRequired(true);
        date.setRequired(true);
        addClassName("income-form");

        binder.bindInstanceFields(this);
        add(
                value,
                name,
                incomeType,
                date,
                createButtonsLayout()
        );

        binder.forField(value).withValidator(value -> value != null && value.toString().length() > 0,"Kwota nie może być pusta!").bind(Income::getValue, Income::setValue);
        binder.forField(name).withValidator(name -> name != null && name.length() > 0,"Tytuł nie może być pusty!").bind(Income::getName, Income::setName);
        binder.forField(incomeType).withValidator(incomeType -> incomeType != null && !incomeType.name().equals(""),"Typ nie może być pusty!").bind(Income::getIncomeType, Income::setIncomeType);
        binder.forField(date).withValidator(date -> date != null && !date.toString().isEmpty(),"Data nie może być pusta!").bind(Income::getDate, Income::setDate);


    }

    public void setIncome(Income income) {
        this.income = income;
        binder.readBean(income);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, income)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(income);
            fireEvent(new SaveEvent(this, income));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class IncomeFormEvent extends ComponentEvent<IncomeForm> {
        private Income income;

        protected IncomeFormEvent(IncomeForm source, Income income) {
            super(source, false);
            this.income = income;
        }

        public Income getIncome() {
            return income;
        }
    }

    public static class SaveEvent extends IncomeFormEvent {
        SaveEvent(IncomeForm source, Income income) {
            super(source, income);
        }
    }

    public static class DeleteEvent extends IncomeFormEvent {
        DeleteEvent(IncomeForm source, Income income) {
            super(source, income);
        }

    }

    public static class CloseEvent extends IncomeFormEvent {
        CloseEvent(IncomeForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
