package com.university.project.ui.views;

import com.university.project.backend.entity.User;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.Optional;

@Route(value = "home", layout = MainView.class)
@PageTitle("Home")
@CssImport("./styles/views/home/home-view.css")
public class HomeView extends Div {
    private final UserService userService;
    private final H2 appName = new H2("Zarządzaj presonalnym lub grupowym budżetem już teraz!");

    private User user;

    public HomeView(UserService userService) {
        this.userService = userService;

        fetchFreshUser();

        setClassName("home-view");

        setUpLayoutWithUserCredentials();
        //createTabs();
    }

    private void setUpLayoutWithUserCredentials() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setClassName("layout-home");

        Div divContainer = new Div();
        Div overlay = new Div();

        divContainer.addClassName("container-home");
        appName.addClassName("style-appName");
        overlay.addClassName("overlay-home");

        divContainer.add(appName,overlay);


        verticalLayout.add(
                divContainer
        );

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