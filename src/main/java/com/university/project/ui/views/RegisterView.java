package com.university.project.ui.views;

import com.university.project.backend.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("register")
@PageTitle("Register | BankAP")
@CssImport("./styles/views/register/register-view.css")
public class RegisterView extends Composite<Component> {
    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected Component initContent() {
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField emailField = createEmail();
        TextField phone = createPhone();

        FormLayout formLayout = new FormLayout(
                username,
                password,
                confirmPassword,
                firstName,
                lastName,
                emailField,
                phone
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        Button buttonGoBack = new Button("Go back", new Icon(VaadinIcon.ARROW_LEFT));
        buttonGoBack.addClickListener(click -> UI.getCurrent().navigate("login"));
        H1 header = new H1("Kontroluj swoje wydatki z nami!");
        Div wrapperHeader = new Div(buttonGoBack, header);
        wrapperHeader.addClassName("wrapper-layout");

        VerticalLayout verticalLayout = new VerticalLayout(
                wrapperHeader,
                formLayout,
                new Button("Sign up", e -> register(
                        username.getValue(),
                        password.getValue(),
                        confirmPassword.getValue(),
                        firstName.getValue(),
                        lastName.getValue(),
                        emailField.getValue(),
                        phone.getValue()
                )));

        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return verticalLayout;
    }

    private void register(String username, String password, String confirmPassword, String firstName,
                          String lastName,String email, String phone) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter a username");
        } else if(password.isEmpty()) {
            Notification.show("Enter a password");
        } else if(!password.equals(confirmPassword)) {
            Notification.show("Passwords don't match!");
        } else if(firstName.isEmpty()) {
            Notification.show("Empty firstname!");
        } else if(lastName.isEmpty()) {
            Notification.show("Empty lastName!");
        } else if(!Character.isUpperCase(firstName.charAt(0))) {
            Notification.show("Capitalize first letter in your firstname!");
        } else if(!Character.isUpperCase(lastName.charAt(0))) {
            Notification.show("Capitalize first letter in your lastname!");
        } else if(!email.matches("^([a-zA-Z0-9_\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$")){
            Notification.show("Email is not correct!");
        }else if((phone.length() != 9)){
            Notification.show("Phone number must consist of 9 digits!");
        }
        else {
            authService.register(username, password, firstName, lastName, email, phone);
            Notification.show("Registration succeeded.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                UI.getCurrent().navigate("login");
            }
        }
    }

    private EmailField createEmail() {
        EmailField emailField = new EmailField("Email");
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Please enter a valid email address");
        return emailField;
    }

    private TextField createPhone() {
        TextField phone = new TextField("Phone");
        phone.setPattern("[0-9]*");
        phone.setPreventInvalidInput(true);
        phone.setMaxLength(9);
        phone.setPlaceholder("123456789");

        return phone;
    }

}
