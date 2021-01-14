package com.university.project.ui.views;

import com.university.project.backend.entity.Expense;
import com.university.project.backend.entity.ExpenseType;
import com.university.project.backend.entity.Income;
import com.university.project.backend.entity.User;
import com.university.project.backend.form.ExpenseForm;
import com.university.project.backend.form.IncomeForm;
import com.university.project.backend.service.ExpenseService;
import com.university.project.backend.service.IncomeService;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
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

    NumberField filterTextValue = new NumberField();
    TextField filterTextName = new TextField();
    ComboBox filterTextType = new ComboBox();
    DatePicker filterTextDate = new DatePicker();
    TextField filterTextUser = new TextField();

    public PersonalBudgetView(UserService userService, ExpenseService expenseService, IncomeService incomeService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;

        fetchFreshUser();

        setId("personal-view");
        setUpTabLayout();
        setUpTabPieChart();
        configureGrid();
        filterTextType.setItems(ExpenseType.values());
        expenseForm = new ExpenseForm();
        expenseForm.addListener(ExpenseForm.SaveEvent.class, this::saveExpense);
        expenseForm.addListener(ExpenseForm.DeleteEvent.class, this::deleteExpense);
        expenseForm.addListener(ExpenseForm.CloseEvent.class, e -> closeEditor());
        expenseGrid.setHeightByRows(true);
        HorizontalLayout content = new HorizontalLayout(expenseGrid, expenseForm);
        content.addClassName("content");
        content.setSizeFull();
        content.setWidthFull();
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

    private void setUpTabPieChart() {
        HorizontalLayout layout = new HorizontalLayout();
        Chart chart = new Chart(ChartType.PIE);
        Configuration config = chart.getConfiguration();
        config.setTitle("Rozkład wydatków na typ:");
        config.setSubTitle("Twoje personalne/dla grupy xyz");
        DataSeries series = new DataSeries("Rozkład");
        series.add(new DataSeriesItem("Podróże", 3000.0));
        series.add(new DataSeriesItem("Prezenty", 1000.0));
        series.add(new DataSeriesItem("Zdrowie", 2000.0));
        series.add(new DataSeriesItem("Edukacja", 750.0));
        series.add(new DataSeriesItem("Jedzenie", 5000.0));
        series.add(new DataSeriesItem("Hobby", 500.0));
        series.add(new DataSeriesItem("Rodzina", 1234.0));
        series.add(new DataSeriesItem("Dom", 3500.0));
        config.addSeries(series);
        layout.add(chart);
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
        filterTextType.addValueChangeListener(e -> updateList());

        filterTextDate.setPlaceholder("Filtruj date");
        filterTextDate.setClearButtonVisible(true);
        filterTextDate.addValueChangeListener(e -> updateList());

        Button addExpenseButton = new Button("Dodaj wydatek", click -> addExpense());
        Button closeFormButton = new Button("Zamknij formularz", click -> closeEditor());
        HorizontalLayout toolbar = new HorizontalLayout(filterTextValue,filterTextName,filterTextType,filterTextDate,addExpenseButton, closeFormButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        return toolbar;
    }

    private void addExpense() {
        expenseGrid.asSingleSelect().clear();
        editExpense(new Expense());
    }

    private void configureGrid() {
        expenseGrid.addClassName("expense-grid");
        expenseGrid.setSizeFull();
        expenseGrid.setColumns("value", "name","expenseType","date");

        expenseGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        expenseGrid.asSingleSelect().addValueChangeListener(evt -> editExpense(evt.getValue()));
    }

    private void editExpense(Expense expense) {
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
        list.retainAll(expenseService.findAllValue(filterTextValue.getValue()));
        list.retainAll(expenseService.findAllName(filterTextName.getValue()));
        list.retainAll(expenseService.findAllDate(filterTextDate.getValue()));
        list.retainAll(expenseService.findAllExpenseType(filterTextType.getValue()));
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