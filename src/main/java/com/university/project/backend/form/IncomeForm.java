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

public class IncomeForm extends FormLayout {

    NumberField kwota = new NumberField("kwota");
    TextField tytul = new TextField("nazwisko");
    DatePicker data = new DatePicker("data");
    ComboBox<IncomeType> typ = new ComboBox<>("typ");

    Button save = new Button("Zapisz");
    Button delete = new Button("Usuń");
    Button close = new Button("Anuluj");

    Binder<Income> binder = new Binder<>(Income.class);
    private Income income;

    public IncomeForm() {
        tytul.setRequired(true);
        data.setRequired(true);
        addClassName("income-form");

        binder.bindInstanceFields(this);
        add(
                kwota,
                tytul,
                typ,
                data,
                createButtonsLayout()
        );
        typ.setItems(IncomeType.values());
        binder.forField(typ).withValidator(typ -> !typ.equals(""),"Typ nie może być pusty!").bind(Income::getIncomeType, Income::setIncomeType);
        binder.forField(kwota).withValidator(kwota -> kwota.toString().length() > 0,"Kwota nie może być pusta!").bind(Income::getValue, Income::setValue);
        binder.forField(tytul).withValidator(tytul -> tytul.length() > 0,"Tytuł nie może być puste!").bind(Income::getName, Income::setName);
        binder.forField(data).withValidator(data -> !data.equals(""),"Data nie może być pusta!").bind(Income::getDate, Income::setDate);


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
