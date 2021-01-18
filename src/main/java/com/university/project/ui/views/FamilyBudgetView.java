package com.university.project.ui.views;

import com.university.project.backend.entity.*;
import com.university.project.backend.form.ExpenseForm;
import com.university.project.backend.form.IncomeForm;
import com.university.project.backend.service.ExpenseService;
import com.university.project.backend.service.FamilyService;
import com.university.project.backend.service.IncomeService;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.university.project.utils.TransformUtils.roundOff;

@PageTitle("Family Budget")
@Route(value = "family-budget", layout = MainView.class)
public class FamilyBudgetView extends VerticalLayout implements HasUrlParameter<Integer> {

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserService userService;

    private User user;
    private Family selectedFamily;
    private final User activeUser = VaadinSession.getCurrent().getAttribute(User.class);

    private final ExpenseForm expenseForm = new ExpenseForm();
    private final IncomeForm incomeForm = new IncomeForm();

    private final Grid<Expense> expenseGrid = new Grid<>(Expense.class);
    private final Grid<Income> incomeGrid = new Grid<>(Income.class);

    private final NumberField filterExpenseValue = new NumberField();
    private final TextField filterExpenseName = new TextField();
    private final ComboBox filterExpenseType = new ComboBox();
    private final DatePicker filterExpenseDate = new DatePicker();

    private final NumberField filterIncomeValue = new NumberField();
    private final TextField filterIncomeName = new TextField();
    private final ComboBox filterIncomeType = new ComboBox();
    private final DatePicker filterIncomeDate = new DatePicker();


    private final Tab tabExpenses = new Tab("Wydatki");
    private final Tab tabIncomes = new Tab("Przychody");
    private final Tab tabTotal = new Tab("Całość");
    private final Tabs tabs = new Tabs(tabExpenses, tabIncomes, tabTotal);
    private final VerticalLayout mainLayoutExpenses = new VerticalLayout();
    private final VerticalLayout mainLayoutIncomes = new VerticalLayout();
    private final VerticalLayout mainLayoutTotal = new VerticalLayout();
    private final Div pages = new Div(mainLayoutExpenses, mainLayoutIncomes, mainLayoutTotal);

    private final Button addExpenseButton = new Button("Dodaj wydatek", new Icon(VaadinIcon.ARROW_RIGHT));
    private final Button addIncomeButton = new Button("Dodaj przychód", new Icon(VaadinIcon.ARROW_LEFT));

    private final List<Double> listExpenses = new ArrayList<>();
    private final List<Double> listIncomes = new ArrayList<>();
    private double totalExpenses = 0.0;
    private double totalIncomes = 0.0;

    private List<Expense> allExpenses = new ArrayList<>();
    private List<Income> allIncomes = new ArrayList<>();
    private final List<String> expenseTypes = Arrays.asList("Transport", "Zdrowie", "Rodzina", "Zakupy", "Prezenty", "Edukacja", "Dom", "Hobby");
    private final List<String> incomeTypes = Arrays.asList("Pensja", "Premia", "Prezent", "Zwrot");

    private final Chart chartExpenses = new Chart(ChartType.PIE);
    private final Chart chartIncomes = new Chart(ChartType.PIE);

    private final H4 expenseTotal = new H4();
    private final H4 expenseTransport = new H4();
    private final H4 expenseHealth = new H4();
    private final H4 expenseFamily = new H4();
    private final H4 expenseGroceries = new H4();
    private final H4 expenseGifts = new H4();
    private final H4 expenseEducation = new H4();
    private final H4 expenseHome = new H4();
    private final H4 expenseHobby = new H4();

    private final H4 incomeTotal = new H4();
    private final H4 incomeSalary = new H4();
    private final H4 incomeBonus = new H4();
    private final H4 incomeGift = new H4();
    private final H4 incomeReturn = new H4();

    private H2 balanceLabel = new H2();
    private H4 incomesLabel = new H4();
    private H4 expensesLabel = new H4();

    public FamilyBudgetView() {
        removeAll();
    }

    private void fetchUserById() throws PersonalBudgetView.UserNotFoundException {
        Integer userId = (Integer) VaadinSession.getCurrent().getAttribute(Constants.USER_ID);
        Optional<User> fetchedUpdatedUser = userService.get(userId);
        if (fetchedUpdatedUser.isPresent()) {
            user = fetchedUpdatedUser.get();
        } else {
            throw new PersonalBudgetView.UserNotFoundException();
        }
    }

    private void fetchAllFamilyExpenses() {
        allExpenses = expenseService.getAllByBudget(selectedFamily.getBudget());
    }

    private void fetchAllFamilyIncomes() {
        allIncomes = incomeService.getAllByBudget(selectedFamily.getBudget());
    }

    private void fetchFreshUser() {
        try {
            fetchUserById();
        } catch (PersonalBudgetView.UserNotFoundException e) {
            System.out.println("User has not been found!");
        }
    }
    private void setUpExpenseLayout() {
        configureExpenseGrid();
        setUpExpensePieChart();

        filterExpenseType.setItems(ExpenseType.values());
        expenseForm.addListener(ExpenseForm.SaveEvent.class, this::saveExpense);
        expenseForm.addListener(ExpenseForm.DeleteEvent.class, this::deleteExpense);
        expenseForm.addListener(ExpenseForm.CloseEvent.class, e -> closeExpenseEditor());
        expenseGrid.setHeightByRows(true);

        HorizontalLayout content = new HorizontalLayout(expenseGrid, expenseForm);
        content.addClassName("content");
        content.setSizeFull();
        content.setWidthFull();
        mainLayoutExpenses.add(getExpenseToolBar(), content);
        closeExpenseEditor();
    }

    private void setUpIncomeLayout() {
        configureIncomeGrid();
        setUpIncomePieChart();

        filterIncomeType.setItems(IncomeType.values());
        incomeForm.addListener(IncomeForm.SaveEvent.class, this::saveIncome);
        incomeForm.addListener(IncomeForm.DeleteEvent.class, this::deleteIncome);
        incomeForm.addListener(IncomeForm.CloseEvent.class, e -> closeIncomeEditor());
        incomeGrid.setHeightByRows(true);

        HorizontalLayout content = new HorizontalLayout(incomeGrid, incomeForm);
        content.setSizeFull();
        content.setWidthFull();
        mainLayoutIncomes.add(getIncomeToolBar(), content);
        closeIncomeEditor();
    }

    private void setUpTotalLayout() {
        var balance = roundOff(totalIncomes - totalExpenses);
         balanceLabel = new H2("Saldo " + (balance));
         incomesLabel = new H4("Przychody " + totalIncomes);
         expensesLabel = new H4("Wydatki " + totalExpenses);

        mainLayoutTotal.add(
                balanceLabel,
                incomesLabel,
                expensesLabel
        );
        mainLayoutTotal.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void updateBalance(){
        mainLayoutTotal.removeAll();
        var balance = roundOff(totalIncomes - totalExpenses);
        balanceLabel = new H2("Saldo " + (balance));
        incomesLabel = new H4("Przychody " + totalIncomes);
        expensesLabel = new H4("Wydatki " + totalExpenses);
        mainLayoutTotal.add(balanceLabel,incomesLabel,expensesLabel);
    }

    private void setUpTabs() {
        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tabExpenses, mainLayoutExpenses);
        tabsToPages.put(tabIncomes, mainLayoutIncomes);
        tabsToPages.put(tabTotal, mainLayoutTotal);

        tabs.setWidthFull();
        tabs.setFlexGrowForEnclosedTabs(1);
        pages.setSizeFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
        });
        add(tabs, pages);

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer id) {
        //beforeEvent.getUI().removeAll();
        selectedFamily = familyService.get(id).get();
        setViewContent();
    }

    private void setViewContent() {
        fetchFreshUser();
        fetchAllFamilyExpenses();
        fetchAllFamilyIncomes();
        setUpTabs();
        this.tabs.setSelectedTab(tabIncomes);
        this.tabs.setSelectedTab(tabExpenses);
        setUpExpenseLayout();
        setUpIncomeLayout();
        setUpTotalLayout();
    }

    private void configureExpenseGrid() {
        expenseGrid.addClassName("expense-grid");
        expenseGrid.setSizeFull();
        expenseGrid.setColumns("name", "value", "expenseType", "date");

        expenseGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        expenseGrid.asSingleSelect().addValueChangeListener(evt -> editExpense(evt.getValue()));
        expenseGrid.setItems(allExpenses);
    }

    private void setUpExpensePieChart() {
        HorizontalLayout wrapperOnVLAndChart = new HorizontalLayout();
        wrapperOnVLAndChart.setWidth("75%");
        wrapperOnVLAndChart.setHeight("50%");

        VerticalLayout vlExpensesWithValue = new VerticalLayout();

        HorizontalLayout vlExpenses = new HorizontalLayout();

        VerticalLayout vlExpComponent1 = new VerticalLayout(
                expenseTransport,
                expenseHealth,
                expenseFamily,
                expenseGroceries
        );

        VerticalLayout vlExpComponent2 = new VerticalLayout(
                expenseGifts,
                expenseEducation,
                expenseHome,
                expenseHobby
        );

        vlExpenses.setWidth("100%");
        vlExpComponent1.setWidth("50%");
        vlExpComponent2.setWidth("50%");

        vlExpenses.add(vlExpComponent1);
        vlExpenses.add(vlExpComponent2);

        expenseTotal.addClassName("text-exp-inc-total");
        vlExpenses.addClassName("text-exp-inc");
        vlExpensesWithValue.add(expenseTotal,vlExpenses);

        fillUpListOfExpensesPerType();
        setUpExpensesDividedByCategoryInVL();

        HorizontalLayout chartLayout = new HorizontalLayout();
        chartLayout.setAlignItems(FlexComponent.Alignment.START);

        updateExpenseChartData();

        chartLayout.add(chartExpenses);
        wrapperOnVLAndChart.add(vlExpensesWithValue, chartLayout);
        mainLayoutExpenses.add(wrapperOnVLAndChart);
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

    private void setUpExpensesDividedByCategoryInVL() {

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
        DataSeries series = new DataSeries();
        boolean flag = false;
        for (int i = 0; i < 8; i++) {
            if (listExpenses.get(i) != 0) {
                series.add(new DataSeriesItem(expenseTypes.get(i), listExpenses.get(i)));
                flag = true;
            }
        }
        if (!flag) series.add(new DataSeriesItem("Brak wydatków",1));
        Configuration config = chartExpenses.getConfiguration();
        config.setTitle("Rozkład wydatków względem typu:");
        config.setSeries(series);
        chartExpenses.drawChart();
    }

    private void saveExpense(ExpenseForm.SaveEvent evt) {
        Expense expenseToSave = evt.getExpense();
        expenseToSave.setBudget(selectedFamily.getBudget());
        expenseService.save(expenseToSave);
        updateBalance();
        refreshAllExpensesViews();
        closeExpenseEditor();
    }

    private void deleteExpense(ExpenseForm.DeleteEvent evt) {
        expenseService.delete(evt.getExpense());
        refreshAllExpensesViews();
        closeExpenseEditor();
    }

    private void refreshAllExpensesViews() {
        fetchAllFamilyExpenses();
        updateListOfExpenses();
        fillUpListOfExpensesPerType();
        setUpExpensesDividedByCategoryInVL();
        updateExpenseChartData();
    }

    private HorizontalLayout getExpenseToolBar() {
        filterExpenseValue.setPlaceholder("Filtruj kwote");
        filterExpenseValue.setClearButtonVisible(true);
        filterExpenseValue.setValueChangeMode(ValueChangeMode.LAZY);
        filterExpenseValue.addValueChangeListener(e -> updateListOfExpenses());

        filterExpenseName.setPlaceholder("Filtruj opis");
        filterExpenseName.setClearButtonVisible(true);
        filterExpenseName.setValueChangeMode(ValueChangeMode.LAZY);
        filterExpenseName.addValueChangeListener(e -> updateListOfExpenses());

        filterExpenseType.setPlaceholder("Filtruj typ");
        filterExpenseType.setClearButtonVisible(true);
        filterExpenseType.addValueChangeListener(e -> updateListOfExpenses());

        filterExpenseDate.setPlaceholder("Filtruj date");
        filterExpenseDate.setClearButtonVisible(true);
        filterExpenseDate.addValueChangeListener(e -> updateListOfExpenses());

        addExpenseButton.addClickListener(click -> {
            if (expenseForm.isVisible()) {
                closeExpenseEditor();
                click.getSource().setText("Dodaj wydatek");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
            } else {
                openExpenseEditor();
                click.getSource().setText("Zamknij fromularz");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_LEFT));
            }
        });
        addExpenseButton.setIconAfterText(true);

        HorizontalLayout toolbar = new HorizontalLayout(filterExpenseValue, filterExpenseName, filterExpenseType, filterExpenseDate, addExpenseButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        return toolbar;
    }

    private void editExpense(Expense expense) {
        if (expense == null) {
            closeExpenseEditor();
        } else {
            expenseForm.setExpense(expense);
            expenseForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void openExpenseEditor() {
        expenseGrid.asSingleSelect().clear();
        editExpense(new Expense());
    }

    private void closeExpenseEditor() {
        expenseForm.setExpense(null);
        expenseForm.setVisible(false);
        removeClassName("editing");

        addExpenseButton.setText("Dodaj wydatek");
        addExpenseButton.setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
    }

    private void updateListOfExpenses() {
        List<Expense> filteredList = new ArrayList<>(allExpenses);
        if (filterExpenseValue.getValue() != null && filterExpenseValue.getValue() != 0.0)
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getValue() == filterExpenseValue.getValue()).collect(Collectors.toUnmodifiableList()));
        if (filterExpenseName.getValue() != null && !filterExpenseName.getValue().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getName().equalsIgnoreCase(filterExpenseName.getValue())).collect(Collectors.toUnmodifiableList()));
        if (filterExpenseDate.getValue() != null && !filterExpenseDate.getValue().toString().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getDate().equals(filterExpenseDate.getValue())).collect(Collectors.toUnmodifiableList()));
        if (filterExpenseType.getValue() != null && !filterExpenseType.getValue().toString().isEmpty())
            filteredList.retainAll(allExpenses.stream().filter(ex -> ex.getExpenseType() == filterExpenseType.getValue()).collect(Collectors.toUnmodifiableList()));
        expenseGrid.setItems(filteredList);
    }

    private void configureIncomeGrid() {
        incomeGrid.addClassName("income-grid");
        incomeGrid.setSizeFull();
        incomeGrid.setColumns("name", "value", "incomeType", "date");

        incomeGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        incomeGrid.asSingleSelect().addValueChangeListener(event -> editIncome(event.getValue()));
        incomeGrid.setItems(allIncomes);
    }

    private void saveIncome(IncomeForm.SaveEvent evt) {
        Income incomeToSave = evt.getIncome();
        incomeToSave.setBudget(selectedFamily.getBudget());
        incomeService.save(incomeToSave);
        updateBalance();
        refreshAllIncomeViews();
        closeIncomeEditor();
    }

    private void deleteIncome(IncomeForm.DeleteEvent evt) {
        incomeService.delete(evt.getIncome());
        refreshAllIncomeViews();
        closeIncomeEditor();
    }

    private void refreshAllIncomeViews() {
        fetchAllFamilyIncomes();
        updateListOfIncomes();
        fillUpListOfIncomesPerType();
        setUpIncomesDividedByCategoryInVL();
        updateIncomeChartData();
    }

    private HorizontalLayout getIncomeToolBar() {
        filterIncomeName.setPlaceholder("Filtruj opis");
        filterIncomeName.setClearButtonVisible(true);
        filterIncomeName.setValueChangeMode(ValueChangeMode.LAZY);
        filterIncomeName.addValueChangeListener(e -> updateListOfIncomes());

        filterIncomeValue.setPlaceholder("Filtruj kwotę");
        filterIncomeValue.setClearButtonVisible(true);
        filterIncomeValue.setValueChangeMode(ValueChangeMode.LAZY);
        filterIncomeValue.addValueChangeListener(e -> updateListOfIncomes());

        filterIncomeType.setPlaceholder("Filtruj typ");
        filterIncomeType.setClearButtonVisible(true);
        filterIncomeType.addValueChangeListener(e -> updateListOfIncomes());

        filterIncomeDate.setPlaceholder("Filtruj datę");
        filterIncomeDate.setClearButtonVisible(true);
        filterIncomeDate.addValueChangeListener(e -> updateListOfIncomes());

        addIncomeButton.addClickListener(click -> {
            if (incomeForm.isVisible()) {
                closeIncomeEditor();
                click.getSource().setText("Dodaj przychód");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
            } else {
                openIncomeEditor();
                click.getSource().setText("Zamknij fromularz");
                click.getSource().setIcon(new Icon(VaadinIcon.ARROW_LEFT));
            }
        });
        addIncomeButton.setIconAfterText(true);
        HorizontalLayout toolbar = new HorizontalLayout(filterIncomeName, filterIncomeValue, filterIncomeType, filterIncomeDate, addIncomeButton);
        toolbar.addClassName("income-toolbar");
        toolbar.setWidthFull();

        return toolbar;
    }

    private void editIncome(Income income) {
        if (income == null) {
            closeIncomeEditor();
        } else {
            incomeForm.setIncome(income);
            incomeForm.setVisible(true);
        }
    }

    private void openIncomeEditor() {
        incomeGrid.asSingleSelect().clear();
        editIncome(new Income());
    }

    private void closeIncomeEditor() {
        incomeForm.setIncome(null);
        incomeForm.setVisible(false);

        addIncomeButton.setText("Dodaj przychód");
        addIncomeButton.setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
    }

    private void setUpIncomePieChart() {
        HorizontalLayout wrapperOnVlAndChart = new HorizontalLayout();
        wrapperOnVlAndChart.setWidth("75%");
        wrapperOnVlAndChart.setHeight("50%");

        VerticalLayout vlIncomesWithValue = new VerticalLayout();

        HorizontalLayout vlIncomes = new HorizontalLayout();

        VerticalLayout vlIncComponent1 = new VerticalLayout(
                incomeSalary,
                incomeBonus
        );

        VerticalLayout vlIncComponent2 = new VerticalLayout(
                incomeGift,
                incomeReturn
        );

        vlIncomes.setWidth("100%");
        vlIncComponent1.setWidth("50%");
        vlIncComponent2.setWidth("50%");

        vlIncomes.add(vlIncComponent1);
        vlIncomes.add(vlIncComponent2);

        incomeTotal.addClassName("text-exp-inc-total");
        vlIncomes.addClassName("text-exp-inc");
        vlIncomesWithValue.add(incomeTotal,vlIncomes);
        fillUpListOfIncomesPerType();
        setUpIncomesDividedByCategoryInVL();

        HorizontalLayout chartLayout = new HorizontalLayout();
        chartLayout.setAlignItems(FlexComponent.Alignment.START);

        updateIncomeChartData();
        chartLayout.add(chartIncomes);
        wrapperOnVlAndChart.add(vlIncomesWithValue,chartLayout);
        mainLayoutIncomes.add(wrapperOnVlAndChart);
    }

    private void fillUpListOfIncomesPerType() {
        totalIncomes = 0.0;
        listIncomes.clear();

        for (IncomeType element: IncomeType.values()) {
            List<Income> filteredList = allIncomes.stream().filter(inc -> inc.getIncomeType() == element).collect(Collectors.toList());
            var incomePerType = filteredList.stream().mapToDouble(Income::getValue).sum();
            totalIncomes += incomePerType;
            listIncomes.add(incomePerType);
        }
    }

    private void setUpIncomesDividedByCategoryInVL() {
        incomeTotal.setText("Wszystkie przychody: " + roundOff(totalIncomes));
        incomeSalary.setText("Pensja " + listIncomes.get(0));
        incomeBonus.setText("Premia " + listIncomes.get(1));
        incomeGift.setText("Prezent " + listIncomes.get(2));
        incomeReturn.setText("Zwrot " + listIncomes.get(3));
    }

    private void updateIncomeChartData() {
        DataSeries series = new DataSeries();
        boolean flag = false;
        for (int i = 0; i < IncomeType.values().length; i++) {
            if (listIncomes.get(i) != 0.0) {
                series.add(new DataSeriesItem(incomeTypes.get(i), listIncomes.get(i)));
                flag = true;
            }
        }
        if (!flag) series.add(new DataSeriesItem("Brak Przychodów",1));
        Configuration config = chartIncomes.getConfiguration();
        config.setTitle("Rozkład przychodow względem typu:");
        config.setSeries(series);
        chartIncomes.drawChart();
    }

    private void updateListOfIncomes() {
        List<Income> filteredList = new ArrayList<>(allIncomes);
        if (filterIncomeValue.getValue() != null && filterIncomeValue.getValue() != 0.0)
            filteredList.retainAll(allIncomes.stream().filter(in -> in.getValue() == filterIncomeValue.getValue()).collect(Collectors.toList()));
        if(filterIncomeName.getValue() != null && !filterIncomeName.getValue().isEmpty())
            filteredList.retainAll(allIncomes.stream().filter(in -> in.getName().equalsIgnoreCase(filterIncomeName.getValue())).collect(Collectors.toList()));
        if (filterIncomeDate.getValue() != null && !filterIncomeDate.getValue().toString().isEmpty())
            filteredList.retainAll(allIncomes.stream().filter(in -> in.getDate().equals(filterIncomeDate.getValue())).collect(Collectors.toList()));
        if (filterIncomeType.getValue() != null && !filterIncomeType.getValue().toString().isEmpty())
            filteredList.retainAll(allIncomes.stream().filter(in -> in.getIncomeType() == filterIncomeType.getValue()).collect(Collectors.toList()));

        incomeGrid.setItems(filteredList);
    }
}
