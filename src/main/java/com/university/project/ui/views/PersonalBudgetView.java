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

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

  Tabs tabs;
    List<Integer> listExpenses = new ArrayList<>();
    List<Integer> listIncomes= new ArrayList<>();
    int totalExpenses;

    public PersonalBudgetView(UserService userService, ExpenseService expenseService, IncomeService incomeService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;

        fetchFreshUser();
        setId("personal-view");
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

        HorizontalLayout layout = new HorizontalLayout();
        HorizontalLayout chartLayout = new HorizontalLayout();
        HorizontalLayout vLayout = new HorizontalLayout();
        vLayout.setWidth("25%");
        vLayout.setHeight("700");
        Chart chart = new Chart(ChartType.PIE);
        Configuration config = chart.getConfiguration();
        config.setTitle("Rozkład wydatków na typ:");
        config.setSubTitle("Twoje personalne/dla grupy xyz");
        DataSeries series = new DataSeries("Rozkład");
        List<String> expenseName = Arrays.asList("Transport","Zdrowie","Rodzina","Zakupy","Prezenty","Edukacja","Dom","Hobby");

        for(int i=0;i<8;i++){
            if(listExpenses.get(i) != 0) series.add(new DataSeriesItem(expenseName.get(i),listExpenses.get(i)));
        }
        config.addSeries(series);

        H4 text = new H4();
        text.setText("Wszystkie wydatki: " + totalExpenses);

        H4 text1 = new H4();
        text1.setText("test " + listExpenses.get(0));

        H4 text2 = new H4();
        text2.setText("test "+ listExpenses.get(1));

        H4 text3 = new H4();
        text3.setText("test "+ listExpenses.get(2));

        H4 text4 = new H4();
        text4.setText("test "+ listExpenses.get(3));

        H4 text5 = new H4();
        text5.setText("test "+ listExpenses.get(4));

        H4 text6 = new H4();
        text6.setText("test "+ listExpenses.get(5));

        H4 text7 = new H4();
        text7.setText("test "+ listExpenses.get(6));

        H4 text8 = new H4();
        text8.setText("test "+ listExpenses.get(7));

        chartLayout.setAlignItems(FlexComponent.Alignment.START);
        vLayout.add(text,text1,text2,text3,text4,text5,text6,text7,text8);
        chartLayout.add(chart);
        layout.add(vLayout,chartLayout);
        add(layout);
    }

    private void fillListIncomes(){

    }

    private void fillListExpenses(){
        totalExpenses = 0;
        for(ExpenseType element: ExpenseType.values()){

            int money = 0;
            List<Expense> list = new ArrayList<>();
            list.addAll(expenseService.findAllExpenseType(element));
            for(int i=0;i<list.size();i++){
                money += list.get(i).getValue();
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
        list.addAll(expenseService.getAll()); //TODO: add budget id restriction
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