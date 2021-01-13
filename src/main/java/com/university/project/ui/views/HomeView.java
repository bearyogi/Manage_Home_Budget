package com.university.project.ui.views;

import com.university.project.backend.entity.User;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.Optional;

@Route(value = "home", layout = MainView.class)
@PageTitle("Home")
public class HomeView extends Div {
    private final UserService userService;

    private User user;

    public HomeView(UserService userService) {
        this.userService = userService;

        fetchFreshUser();

        setId("home-view");

        setUpLayoutWithUserCredentials();
        //createTabs();
    }

    private void setUpLayoutWithUserCredentials() {
        Label labelFirstName = new Label("First Name: " + user.getFirstName());
        Label labelLastName = new Label("Last Name: " + user.getLastName());
        Label labelEmail = new Label("Email: " + user.getEmail());
        Label labelPhone = new Label("Phone: " + user.getPhone());

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(
                labelFirstName,
                labelLastName,
                labelEmail,
                labelPhone
        );

        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(verticalLayout);
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