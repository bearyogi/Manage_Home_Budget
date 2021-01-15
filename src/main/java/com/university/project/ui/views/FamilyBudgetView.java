package com.university.project.ui.views;

import com.university.project.backend.entity.Family;
import com.university.project.backend.entity.User;
import com.university.project.backend.service.ExpenseService;
import com.university.project.backend.service.FamilyService;
import com.university.project.backend.service.IncomeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

@PageTitle("Family Budget")
@Route(value = "family-budget", layout = MainView.class)
public class FamilyBudgetView extends VerticalLayout implements HasUrlParameter<Integer> {
    //TODO podobnie jak z familyBudget chart i inne peirdoly
    private final FamilyService familyService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    private Family selectedFamily;
    private final User activeUser = VaadinSession.getCurrent().getAttribute(User.class);

    private final Tab tabExpenses = new Tab("Wydatki");
    private final Tab tabIncomes = new Tab("Przychody");
    private final Tab tabTotal = new Tab("Całość");
    private final Tabs tabs = new Tabs(tabExpenses, tabIncomes, tabTotal);
    private final VerticalLayout mainLayoutExpenses = new VerticalLayout();
    private final VerticalLayout mainLayoutIncomes = new VerticalLayout();
    private final VerticalLayout mainLayoutTotal = new VerticalLayout();
    private final Div pages = new Div(mainLayoutExpenses, mainLayoutIncomes, mainLayoutTotal);


    public FamilyBudgetView(FamilyService familyService, ExpenseService expenseService, IncomeService incomeService) {
        this.familyService = familyService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer id) {
        selectedFamily = familyService.get(id).get();
        setViewContent();
    }

    private void setViewContent() {
        addReturnButton();
        addTabsWithPages();
        addContent();
    }

    private void addReturnButton() {
        Button buttonReturn = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        buttonReturn.addClickListener(click -> {
            UI.getCurrent().navigate(MyFamiliesView.class);
        });

        add(buttonReturn);
    }

    private void addTabsWithPages() {
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

    private void addContent() {
        add(new H4("Podzial na uzytkownikow wedlug wydatków"));
    }
}
