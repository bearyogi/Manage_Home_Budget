package com.university.project.ui.views;

import com.university.project.backend.service.AuthService;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;

@UIScope
@Route(value = "login")
@PageTitle("Login | ManageBudget ")
@CssImport("./styles/views/login/login-view.css")
public class LoginView extends Div {

    public LoginView(AuthService authService) {
        ToggleButton toggleButton = new ToggleButton("Przełącz ciemny tryb", click -> {
            ThemeList themeList =  UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)) themeList.remove(Lumo.DARK);
            else themeList.add(Lumo.DARK);
        });
        setId("login-view");
        setMinWidth("40%");
        setMaxWidth("30%");

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        password.setId("login-view-password");

        add(
                toggleButton,
                new H1("Welcome"),
                username,
                password,
                new Button("Login", event -> {
                    try {
                        authService.authenticate(username.getValue(), password.getValue());
                        UI.getCurrent().navigate("home");
                    } catch (AuthService.AuthException e) {
                        Notification.show("Wrong credentials.");
                    }
                }),
                new RouterLink("Register", RegisterView.class)
        );
    }

}
