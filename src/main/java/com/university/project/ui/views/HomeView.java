package com.university.project.ui.views;

import com.university.project.ui.components.MainViewBus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Route(value = "home", layout = MainView.class)
@PageTitle("Home")
@CssImport("./styles/views/home/home-view.css")
public class HomeView extends Div {
    private final MainViewBus mainViewBus;

    private final Div textHome1 = new Div();
    private final Div textHome2 = new Div();

    public HomeView(MainViewBus mainViewBus) {
        this.mainViewBus = mainViewBus;

        Span textTop1 = new Span("Właściwe zarządzanie");
        Span textTop2 = new Span("swoim budżetem");

        textHome1.add(textTop1, textTop2);
        textHome2.setText("to podstawa!");

        setClassName("home-view");

        setWidth("100%");
        setHeight("100%");

        setUpGreeting();
    }

    private void setUpGreeting() {
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

        Button routerLink = new Button("Rozpocznij");
        routerLink.addClickListener(click -> mainViewBus.getMainView().navigateAndSelectTabInMainView(2, PersonalBudgetView.class));
        routerLink.setClassName("btn");
        card.add(routerLink);

        container.add(titleAnimation,card);
        add(container);
    }
}