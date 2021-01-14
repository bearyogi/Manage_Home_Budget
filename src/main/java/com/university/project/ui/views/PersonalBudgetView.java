package com.university.project.ui.views;

import com.university.project.backend.entity.*;
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

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.*;

@Route(value = "personal", layout = MainView.class)
@PageTitle("PersonalBudget")
public class PersonalBudgetView extends Div {
    private final UserService userService;

    private User user;

    private ExpenseForm expenseForm;
    private IncomeForm incomeForm;

    private ExpenseService expenseService;
    private IncomeService incomeService;

    private Grid<Expense> expenseGrid = new Grid<>(Expense.class);
    private Grid<Income> incomeGrid = new Grid<>(Income.class);

    private NumberField filterTextValue = new NumberField();
    private TextField filterTextName = new TextField();
    private ComboBox filterTextType = new ComboBox();
    private DatePicker filterTextDate = new DatePicker();

    private Tabs tabs;
    private List<Integer> listExpenses = new ArrayList<>();
    private List<Integer> listIncomes = new ArrayList<>();
    private int totalExpenses;

    private final H4 expenseTotal = new H4();
    private final H4 expenseTransport = new H4();
    private final H4 expenseHealth = new H4();
    private final H4 expenseFamily = new H4();
    private final H4 expenseGroceries = new H4();
    private final H4 expenseGifts = new H4();
    private final H4 expenseEducation = new H4();
    private final H4 expenseHome = new H4();
    private final H4 expenseHobby = new H4();


    public PersonalBudgetView(UserService userService, ExpenseService expenseService, IncomeService incomeService) {
        addClassName("personal-view");

        this.userService = userService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;
        fetchFreshUser();

        setUpTabLayout();
        fillListExpenses();
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
        tabs = new Tabs(tab1, tab2, tab3);
        tabs.setWidthFull();
        tabs.setFlexGrowForEnclosedTabs(1);
        layout.add(tabs);
        add(layout);
    }

    private void setUpTabPieChart() {

        HorizontalLayout chartLayout = new HorizontalLayout();
        HorizontalLayout vLayout = new HorizontalLayout();
        vLayout.setWidth("75%");
        vLayout.setHeight("50%");
        Chart chart = new Chart(ChartType.PIE);
        Configuration config = chart.getConfiguration();
        config.setTitle("Rozkład wydatków na typ:");
        config.setSubTitle("Twoje personalne/dla grupy xyz");
        DataSeries series = new DataSeries("Rozkład");
        List<String> expenseName = Arrays.asList("Transport", "Zdrowie", "Rodzina", "Zakupy", "Prezenty", "Edukacja", "Dom", "Hobby");

        for (int i = 0; i < 8; i++) {
            if (listExpenses.get(i) != 0)
                series.add(new DataSeriesItem(expenseName.get(i), listExpenses.get(i)));
        }
        config.addSeries(series);

        VerticalLayout vlExpensesWithValue = new VerticalLayout(
                expenseTotal,
                expenseTransport,
                expenseHealth,
                expenseFamily,
                expenseGroceries,
                expenseGifts,
                expenseEducation,
                expenseHome,
                expenseHobby
        );
        setUpExpensesOrderByCategory();

        chartLayout.setAlignItems(FlexComponent.Alignment.START);
        chartLayout.add(chart);
        vLayout.add(vlExpensesWithValue, chartLayout);
        add(vLayout);
    }

    private void setUpExpensesOrderByCategory() {
        expenseTotal.setText("Wszystkie wydatki: " + this.totalExpenses);
        expenseTransport.setText("Transport " + listExpenses.get(0));
        expenseHealth.setText("Zdrowie " + listExpenses.get(1));
        expenseFamily.setText("Rodzina " + listExpenses.get(2));
        expenseGroceries.setText("Zakupy " + listExpenses.get(3));
        expenseGifts.setText("Prezenty " + listExpenses.get(4));
        expenseEducation.setText("Edukacja " + listExpenses.get(5));
        expenseHome.setText("Dom " + listExpenses.get(6));
        expenseHobby.setText("Hobby " + listExpenses.get(7));
    }

    private void fillListIncomes() {

    }

    private void fillListExpenses() {
        totalExpenses = 0;
        for (ExpenseType element : ExpenseType.values()) {

            int money = 0;
            List<Expense> list = new ArrayList<>(expenseService.findAllExpenseType(user.getPrivateBudget(), element));
            for (Expense expense : list) {
                money += expense.getValue();
            }
            totalExpenses += money;
            listExpenses.add(money);
        }
    }

    private void deleteExpense(ExpenseForm.DeleteEvent evt) {
        expenseService.delete(evt.getExpense());
        updateList();
        closeEditor();
    }

    private void saveExpense(ExpenseForm.SaveEvent evt) {
        Expense expenseToSave = evt.getExpense();
        expenseToSave.setBudget(user.getPrivateBudget());
        expenseService.save(expenseToSave);
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
        HorizontalLayout toolbar = new HorizontalLayout(filterTextValue, filterTextName, filterTextType, filterTextDate, addExpenseButton, closeFormButton);
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
        expenseGrid.setColumns("value", "name", "expenseType", "date");

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
        Budget personalBudget = user.getPrivateBudget();
        List<Expense> list = new ArrayList<>(expenseService.getAllByBudget(personalBudget));
        list.retainAll(expenseService.getAllByValue(personalBudget, filterTextValue.getValue()));
        list.retainAll(expenseService.findAllName(personalBudget, filterTextName.getValue()));
        list.retainAll(expenseService.findAllDate(personalBudget, filterTextDate.getValue()));
        list.retainAll(expenseService.findAllExpenseType(personalBudget, filterTextType.getValue()));
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