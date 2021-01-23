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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import java.util.Optional;

@UIScope
@Route(value = "home", layout = MainView.class)
@PageTitle("Home")
@CssImport("./styles/views/home/home-view.css")
public class HomeView extends Div {
    private final UserService userService;
    private final Span textTop1 = new Span("Właściwe zarządzanie");
    private final Span textTop2 = new Span("swoim budżetem");
    private final Div textHome1 = new Div();
    private final Div textHome2 = new Div();

    private User user;

    public HomeView(UserService userService) {
        this.userService = userService;

        fetchFreshUser();

        textHome1.add(textTop1,textTop2);
        textHome2.setText("to podstawa!");

        setClassName("home-view");

        setWidth("100%");
        setHeight("100%");



        setUpLayoutWithUserCredentials();
        //createTabs();
    }

    private void setUpLayoutWithUserCredentials() {
        Div container = new Div();
        container.addClassName("back-home");

        Div titleAnimation = new Div();
        Div top = new Div();
        Div bot = new Div();

        titleAnimation.addClassName("animated-title");
        top.addClassName("text-top");
        bot.addClassName("text-bottom");

        top.add(textHome1);
        bot.add(textHome2);

        titleAnimation.add(top,bot);

        Div card = new Div();

        card.setText("Zarządzaj personalnym oraz grupowym budżetem już teraz!");

        card.setClassName("cardHome");

        RouterLink routerLink = new RouterLink("Rozpocznij", PersonalBudgetView.class);
        routerLink.setClassName("btn");
        card.add(routerLink);

        container.add(titleAnimation,card);
        add(container);
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

    @UIScope
    public static class UserNotFoundException extends Exception {

    }
}