package com.university.project.ui.views;

import com.university.project.backend.entity.User;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;

import static com.university.project.ui.views.HomeView.*;


@Route(value = "credentials", layout = MainView.class)
@CssImport("./styles/views/credentials/credentials-view.css")
@PageTitle("Credentials")
public class CredentialsView extends HorizontalLayout {

    private User user;
    private final UserService userService;

    private final Label labelUsername = new Label();
    private final Label labelFirstName = new Label();
    private final Label labelLastName = new Label();
    private final Label labelEmail = new Label();
    private final Label labelPhone = new Label();

    private final H3 textTitle = new H3("Zmiana danych:");
    private final TextField textFieldUsername = new TextField("Nowy nick");
    private final PasswordField textFieldPassword = new PasswordField("Nowe hasło");
    private final TextField textFieldFirstName = new TextField("Nowe imię");
    private final TextField textFieldLastName = new TextField("Nowe nazwisko");
    private final EmailField textFieldEmail = new EmailField("Nowy email");
    private final TextField textFieldPhone = new TextField("Nowy numer telefonu");

    public CredentialsView(UserService userService) {
        addClassName("credentials-view");
        this.userService = userService;
        setSizeFull();
        fetchActiveUser();

        fillUpLabelsWithUserCredentials();
        setUpTextFields();
        setUpLayout();
    }

    private void fetchActiveUser() {
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

    private void fillUpLabelsWithUserCredentials() {
        labelUsername.setText("Username: " + user.getUsername());
        labelFirstName.setText("First Name: " + user.getFirstName());
        labelLastName.setText("Last Name: " + user.getLastName());
        labelEmail.setText("Email: " + user.getEmail());
        labelPhone.setText("Phone: " + user.getPhone());
    }

    private void setUpTextFields() {
        textFieldPhone.setPattern("[0-9]*");
        textFieldPhone.setPreventInvalidInput(true);
        textFieldPhone.setMaxLength(9);
/*        textFieldPhone.addValueChangeListener(ev -> {
            String currentName = ev.getValue();
            if (currentName != null && !currentName.isEmpty()) {
                if (currentName.length() != 9) {
                    textFieldPhone.setErrorMessage("Wpisz 9 cyfr!");
                    System.out.println(textFieldPhone.getErrorMessage());
                }
            }
        });*/

        textFieldEmail.setErrorMessage("Wpisz poprawny email");

/*        textFieldFirstName.addValueChangeListener(ev -> {
            String currentName = ev.getValue();
            if (currentName != null && !currentName.isEmpty()) {
                if (!currentName.matches("^[A-Z]([a-z])+$"))
                    textFieldFirstName.setErrorMessage("Imię z wielkiej litery!");
            }
        });

        textFieldLastName.addValueChangeListener(ev -> {
            String currentName = ev.getValue();
            if (currentName != null && !currentName.isEmpty()) {
                if (!currentName.matches("^[A-Z]([a-z])+$"))
                    textFieldLastName.setErrorMessage("Nazwisko z wielkiej litery!");
            }
        });*/
    }

    private void setUpLayout() {

        Div Layout = new Div();
        Layout.setClassName("flex");


        Div card = new Div();
        Div container = new Div();

        container.setClassName("container");

        VerticalLayout vlLabels = new VerticalLayout();
        vlLabels.add(
                labelUsername,
                labelFirstName,
                labelLastName,
                labelEmail,
                labelPhone
        );

        container.add(vlLabels);


        vlLabels.setClassName("vl-labels");
        Image avatar = new Image("images/user.svg", "Avatar");
        avatar.setClassName("top-card");

        card.setClassName("card");
        card.add(avatar, container);


        Div cardText = new Div();
        Div containerText = new Div();

        containerText.setClassName("container");

        VerticalLayout vlTextFields = new VerticalLayout();

        Button buttonChangeCredentials = new Button("Zmień dane");
        buttonChangeCredentials.setId("button-change-credentials");
        buttonChangeCredentials.addClickListener(event -> {
            updateUser();
        });

        vlTextFields.add(
                textFieldUsername,
                textFieldPassword,
                textFieldFirstName,
                textFieldLastName,
                textFieldEmail,
                textFieldPhone,
                buttonChangeCredentials
        );

        containerText.add(vlTextFields);
        vlTextFields.setClassName("vl-labels");

        cardText.setClassName("card");
        textTitle.setClassName("text-title");
        cardText.add(textTitle, containerText);

        Layout.add(card, cardText);

        add(Layout);
    }

    private void updateUser() {
        if (textFieldUsername.getValue() != null && !textFieldUsername.isEmpty())
            user.setUsername(textFieldUsername.getValue());

        if (textFieldPassword.getValue() != null && !textFieldPassword.isEmpty()) {
            var plainPassword = textFieldPassword.getValue();
            var passwordSalt = RandomStringUtils.random(32);
            var passwordHash = DigestUtils.sha1Hex(plainPassword + passwordSalt);

            user.setPasswordSalt(passwordSalt);
            user.setPasswordHash(passwordHash);
        }

        if (textFieldFirstName.getValue() != null && !textFieldFirstName.isEmpty()) {
            if (textFieldFirstName.getValue().matches("^[A-Z]([a-z])+$")) {
                user.setFirstName(textFieldFirstName.getValue());
            } else {
                Notification.show("Imię powinno być z wielkiej litery!", 2500, Notification.Position.MIDDLE);
            }
        }

        if (textFieldLastName.getValue() != null && !textFieldLastName.isEmpty()) {
            if (textFieldLastName.getValue().matches("^[A-Z]([a-z])+$")) {
                user.setLastName(textFieldLastName.getValue());
            } else {
                Notification.show("Nazwisko powinno być z wielkiej litery!", 2500, Notification.Position.MIDDLE);
            }
        }

        if (textFieldEmail.getValue() != null && !textFieldEmail.isEmpty()
                && textFieldEmail.getErrorMessage().isEmpty())
            user.setEmail(textFieldEmail.getValue());

        if (textFieldPhone.getValue() != null && !textFieldPhone.isEmpty()) {
            if (textFieldPhone.getValue().length() == 9) {
                user.setPhone(textFieldPhone.getValue());
            } else {
                Notification.show("Telefon powinien mieć 9 cyfr!", 2500, Notification.Position.MIDDLE);
            }
        }

        user = userService.update(user);

        fillUpLabelsWithUserCredentials();
        clearTextFields();
    }

    private void clearTextFields() {
        textFieldFirstName.clear();
        textFieldLastName.clear();
        textFieldUsername.clear();
        textFieldEmail.clear();
        textFieldPhone.clear();
    }

}
