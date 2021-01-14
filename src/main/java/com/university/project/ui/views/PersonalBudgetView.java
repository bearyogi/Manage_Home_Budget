package com.university.project.ui.views;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.Income;
import com.university.project.backend.entity.User;
import com.university.project.backend.form.ExpenseForm;
import com.university.project.backend.form.IncomeForm;
import com.university.project.backend.service.ExpenseService;
import com.university.project.backend.service.IncomeService;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "personal", layout = MainView.class)
@PageTitle("PersonalBudget")
public class PersonalBudgetView extends Div {
    private final UserService userService;

    private User user;

  ExpenseForm expenseForm;
  IncomeForm incomeForm;

  ExpenseService expenseService;
  IncomeService incomeService;

  Grid<Expense> expenseGrid= new Grid<>(Expense.class);
  Grid<Income> incomeGrid = new Grid<>(Income.class);

    TextField filterTextValue = new TextField();
    TextField filterTextName = new TextField();
    TextField filterTextType = new TextField();
    TextField filterTextDate = new TextField();
    TextField filterTextUser = new TextField();

    public PersonalBudgetView(UserService userService, ExpenseService expenseService, IncomeService incomeService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;

        fetchFreshUser();

        setId("personal-view");
        setUpTabLayout();
        configureGrid();

        expenseForm = new ExpenseForm();
        expenseForm.addListener(ExpenseForm.SaveEvent.class, this::saveExpense);
        expenseForm.addListener(ExpenseForm.DeleteEvent.class, this::deleteExpense);
        expenseForm.addListener(ExpenseForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(expenseGrid, expenseForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();

    }

    private void setUpTabLayout() {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setWidthFull();
            Tab tab1 = new Tab("Wydatki");
            Tab tab2 = new Tab("Całość");
            Tab tab3 = new Tab("Przychody");
            Tabs tabs = new Tabs(tab1, tab2, tab3);
            tabs.setWidthFull();
            tabs.setFlexGrowForEnclosedTabs(1);
            layout.add(tabs);
            add(layout);
    }

    private void deleteExpense(ExpenseForm.DeleteEvent evt) {
        expenseService.delete(evt.getExpense());
        updateList();
        closeEditor();
    }

    private void saveExpense(ExpenseForm.SaveEvent evt) {
        expenseService.save(evt.getExpense());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterTextValue.setPlaceholder("Filtruj kwote");
        filterTextValue.setClearButtonVisible(true);
        filterTextValue.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextValue.addValueChangeListener(e -> updateList());

        filterTextName.setPlaceholder("Filtruj opis");
        filterTextName.setClearButtonVisible(true);
        filterTextName.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextName.addValueChangeListener(e -> updateList());

        filterTextType.setPlaceholder("Filtruj typ");
        filterTextType.setClearButtonVisible(true);
        filterTextType.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextType.addValueChangeListener(e -> updateList());

        filterTextDate.setPlaceholder("Filtruj date");
        filterTextDate.setClearButtonVisible(true);
        filterTextDate.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextDate.addValueChangeListener(e -> updateList());

        filterTextUser.setPlaceholder("Filtruj użytkownika");
        filterTextUser.setClearButtonVisible(true);
        filterTextUser.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextUser.addValueChangeListener(e -> updateList());


        Button addSeansButton = new Button("Dodaj seans", click -> addExpense());
        Button closeFormButton = new Button("Zamknij formularz", click -> closeEditor());
        HorizontalLayout toolbar = new HorizontalLayout(filterTextValue,filterTextName,filterTextType,filterTextDate,filterTextUser,addSeansButton, closeFormButton);
        //filterTextSala,filterTextFilm
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addExpense() {
        expenseGrid.asSingleSelect().clear();
        editSeans(new Expense());
    }

    private void configureGrid() {
        expenseGrid.addClassName("expense-grid");
        expenseGrid.setSizeFull();
        expenseGrid.setColumns("value", "name","expenseType","date");

        expenseGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        expenseGrid.asSingleSelect().addValueChangeListener(evt -> editSeans(evt.getValue()));
    }

    private void editSeans(Expense expense) {
        if (expense == null) {
            closeEditor();
        } else {
            expenseForm.setExpense(expense);
            expenseForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        expenseForm.setExpense(null);
        expenseForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        List<Expense> list = new ArrayList<>();
        list.addAll(expenseService.getAll());
//        list.addAll(ExpenseService.findAllLektor(filterTextLektor.getValue()));
//        list.retainAll(seansService.findAllNapisy(filterTextNapisy.getValue()));
//        list.retainAll(seansService.findAllData(filterTextData.getValue()));
//        list.retainAll(seansService.findAllGodzina(filterTextGodzina.getValue()));
//        list.retainAll(seansService.findAllSala(filterTextSala.getValue()));
//        list.retainAll(seansService.findAllFilm(filterTextFilm.getValue()));
        expenseGrid.setItems(list);
    }

    private void fetchFreshUser() {
        try {
            fetchUserById();
        } catch (UserNotFoundException e) {
            System.out.println("User has not been found!");
        }
    }

    private void fetchUserById() throws UserNotFoundException {
        Integer userId = (Integer) VaadinSession.getCurrent().getAttribute(Constants.USER_ID);
        Optional<User> fetchedUpdatedUser = userService.get(userId);
        if (fetchedUpdatedUser.isPresent()) {
            user = fetchedUpdatedUser.get();
        } else {
            throw new UserNotFoundException();
        }
    }


    public static class UserNotFoundException extends Exception {

    }
}