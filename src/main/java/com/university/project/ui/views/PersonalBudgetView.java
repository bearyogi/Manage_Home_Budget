package com.university.project.ui.views;

import com.university.project.backend.entity.*;
import com.university.project.backend.form.ExpenseForm;
import com.university.project.backend.form.IncomeForm;
import com.university.project.backend.service.ExpenseService;
import com.university.project.backend.service.IncomeService;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import java.util.stream.Collectors;

import static com.university.project.utils.TransformUtils.*;

@Route(value = "personal", layout = MainView.class)
@PageTitle("PersonalBudget")
public class PersonalBudgetView extends Div {
    private final UserService userService;

    private User user;

    private final ExpenseForm expenseForm = new ExpenseForm();
    private IncomeForm incomeForm;

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    private final Grid<Expense> expenseGrid = new Grid<>(Expense.class);
    private final Grid<Income> incomeGrid = new Grid<>(Income.class);

    private NumberField filterTextValue = new NumberField();
    private TextField filterTextName = new TextField();
    private ComboBox filterTextType = new ComboBox();
    private DatePicker filterTextDate = new DatePicker();

    private final Tab tabExpenses = new Tab("Wydatki");
    private final Tab tabIncomes = new Tab("Przychody");
    private final Tab tabTotal = new Tab("Całość");
    private final Tabs tabs = new Tabs(tabExpenses, tabIncomes, tabTotal);
    private final Button addExpenseButton = new Button("Dodaj wydatek", new Icon(VaadinIcon.ARROW_RIGHT));

    private List<Double> listExpenses = new ArrayList<>();
    private List<Double> listIncomes = new ArrayList<>();
    private double totalExpenses = 0.0;
    private double totalIncomes = 0.0;

    private List<Expense> allExpenses = new ArrayList<>();
    private final List<String> expenseTypes = Arrays.asList("Transport", "Zdrowie", "Rodzina", "Zakupy", "Prezenty", "Edukacja", "Dom", "Hobby");
    private final Chart chartExpenses = new Chart(ChartType.PIE);


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
        fetchAllUserExpenses();

        setUpTabLayout();
        fillUpListOfExpensesPerType();
        setUpExpensePieChart();
        configureGrid();

        filterTextType.setItems(ExpenseType.values());
        expenseForm.addListener(ExpenseForm.SaveEvent.class, this::saveExpense);
        expenseForm.addListener(ExpenseForm.DeleteEvent.class, this::deleteExpense);
        expenseForm.addListener(ExpenseForm.CloseEvent.class, e -> closeEditor());
        expenseGrid.setHeightByRows(true);

        HorizontalLayout content = new HorizontalLayout(expenseGrid, expenseForm);
        content.addClassName("content");
        content.setSizeFull();
        content.setWidthFull();
        add(getToolBar(), content);
        closeEditor();
    }

    private void fetchAllUserExpenses() {
        allExpenses = expenseService.getAllByBudget(user.getPrivateBudget());
    }

    private void setUpTabLayout() {
        tabs.setWidthFull();
        tabs.setFlexGrowForEnclosedTabs(1);
        add(tabs);
    }

    private void fillUpListOfExpensesPerType() {
        totalExpenses = 0.0;
        listExpenses.clear();

        for (ExpenseType element : ExpenseType.values()) {
            List<Expense> filteredList = allExpenses.stream().filter(expense -> expense.getExpenseType() == element).collect(Collectors.toList());
            var expensePerType = filteredList.stream().mapToDouble(Expense::getValue).sum();
            totalExpenses += expensePerType;
            listExpenses.add(expensePerType);
        }
    }

    private void setUpExpensePieChart() {
        HorizontalLayout wrapperOnVLAndChart = new HorizontalLayout();
        wrapperOnVLAndChart.setWidth("75%");
        wrapperOnVLAndChart.setHeight("50%");

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
        setUpExpensesOrderByCategoryInVL();

        HorizontalLayout chartLayout = new HorizontalLayout();
        chartLayout.setAlignItems(FlexComponent.Alignment.START);

        updateExpenseChartData();

        chartLayout.add(chartExpenses);
        wrapperOnVLAndChart.add(vlExpensesWithValue, chartLayout);
        add(wrapperOnVLAndChart);
    }

    private void setUpExpensesOrderByCategoryInVL() {

        expenseTotal.setText("Wszystkie wydatki: " + roundOff(totalExpenses));
        expenseTransport.setText("Transport " + listExpenses.get(0));
        expenseHealth.setText("Zdrowie " + listExpenses.get(1));
        expenseFamily.setText("Rodzina " + listExpenses.get(2));
        expenseGroceries.setText("Zakupy " + listExpenses.get(3));
        expenseGifts.setText("Prezenty " + listExpenses.get(4));
        expenseEducation.setText("Edukacja " + listExpenses.get(5));
        expenseHome.setText("Dom " + listExpenses.get(6));
        expenseHobby.setText("Hobby " + listExpenses.get(7));
    }

    private void updateExpenseChartData() {
/*
        Configuration config = chartExpenses.getConfiguration();

        config.setTitle("Rozkład wydatków względem typu:");
        config.setSubTitle("Twoje wydatki");
*/

        DataSeries series = new DataSeries();
        for (int i = 0; i < 8; i++) {
            if (listExpenses.get(i) != 0) {
                series.add(new DataSeriesItem(expenseTypes.get(i), listExpenses.get(i)));
            }
        }

        Configuration config = chartExpenses.getConfiguration();
        config.setTitle("Rozkład wydatków względem typu:");
        config.setSeries(series);
        chartExpenses.drawChart();
    }

    private void fillListIncomes() {
        //TODO list of incomes
    }

    private void configureGrid() {
        expenseGrid.addClassName("expense-grid");
        expenseGrid.setSizeFull();
        expenseGrid.setColumns("value", "name", "expenseType", "date");

        expenseGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        expenseGrid.asSingleSelect().addValueChangeListener(evt -> editExpense(evt.getValue()));
        expenseGrid.setItems(allExpenses);
    }

    private void saveExpense(ExpenseForm.SaveEvent evt) {
        Expense expenseToSave = evt.getExpense();
        expenseToSave.setBudget(user.getPrivateBudget());
        expenseService.save(expenseToSave);

        refreshAllExpensesViews();
        closeEditor();
    }

    private void deleteExpense(ExpenseForm.DeleteEvent evt) {
        expenseService.delete(evt.getExpense());
        refreshAllExpensesViews();
        closeEditor();
    }

    private void refreshAllExpensesViews() {
        fetchAllUserExpenses();
        updateList();
        fillUpListOfExpensesPerType();
        setUpExpensesOrderByCategoryInVL();
        updateExpenseChartData();
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

        addExpenseButton.addClickListener(click -> {
            if (expenseForm.isVisible()) {
                closeEditor();
                click.getSource().setText("Dodaj wydatek");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
            } else {
                openEditor();
                click.getSource().setText("Zamknij fromularz");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_LEFT));
            }
        });
        addExpenseButton.setIconAfterText(true);

        HorizontalLayout toolbar = new HorizontalLayout(filterTextValue, filterTextName, filterTextType, filterTextDate, addExpenseButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        return toolbar;
    }

    private void openEditor() {
        expenseGrid.asSingleSelect().clear();
        editExpense(new Expense());
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

        addExpenseButton.setText("Dodaj wydatek");
        addExpenseButton.setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
    }

    private void updateList() {
        List<Expense> filteredList = new ArrayList<>(allExpenses);
        if (filterTextValue.getValue() != null && filterTextValue.getValue() != 0.0)
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getValue() == filterTextValue.getValue()).collect(Collectors.toUnmodifiableList()));
        if (filterTextName.getValue() != null && !filterTextName.getValue().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getName().equalsIgnoreCase(filterTextName.getValue())).collect(Collectors.toUnmodifiableList()));
        if (filterTextDate.getValue() != null && !filterTextDate.getValue().toString().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getDate().equals(filterTextDate.getValue())).collect(Collectors.toUnmodifiableList()));
        if (filterTextType.getValue() != null && !filterTextType.getValue().toString().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getExpenseType() == filterTextType.getValue()).collect(Collectors.toUnmodifiableList()));
        //System.out.println("Filtered list size = " + filteredList.size());
        expenseGrid.setItems(filteredList);
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