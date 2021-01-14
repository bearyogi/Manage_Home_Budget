package com.university.project.ui.views;

import com.university.project.backend.entity.User;
import com.university.project.backend.form.ExpenseForm;
import com.university.project.backend.form.IncomeForm;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.Optional;

@Route(value = "personal", layout = MainView.class)
@PageTitle("PersonalBudget")
public class PersonalBudgetView extends Div {
    private final UserService userService;

    private User user;

  ExpenseForm expenseForm;
  IncomeForm incomeForm;

    public PersonalBudgetView(UserService userService) {
        this.userService = userService;

        fetchFreshUser();

        setId("personal-view");
        setUpTabLayout();
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
    private void setUpGridLayout(){

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