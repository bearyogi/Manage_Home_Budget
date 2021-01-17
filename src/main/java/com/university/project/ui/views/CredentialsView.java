package com.university.project.ui.views;

import com.university.project.backend.entity.User;
import com.university.project.backend.service.UserService;
import com.university.project.utils.Constants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private final TextField textFieldUsername = new TextField("Nowy nick");
    private final PasswordField textFieldPassword = new PasswordField("Nowe hasło");
    private final TextField textFieldFirstName = new TextField("Nowe imię");
    private final TextField textFieldLastName = new TextField("Nowe nazwisko");
    private final TextField textFieldEmail = new TextField("Nowy email");
    private final TextField textFieldPhone = new TextField("Nowy numer telefonu");

    public CredentialsView(UserService userService) {
        addClassName("credentials-view");
        this.userService = userService;
        setSizeFull();

        fetchActiveUser();
        fillUpLabelsWithUserCredentials();
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

    private void setUpLayout() {
        VerticalLayout vlLabels = new VerticalLayout(
                labelUsername,
                labelFirstName,
                labelLastName,
                labelEmail,
                labelPhone
        );

        vlLabels.setAlignItems(Alignment.END);
        vlLabels.setId("vl-labels");

        Button buttonChangeCredentials = new Button("Zmień dane");
        buttonChangeCredentials.setId("button-change-credentials");
        buttonChangeCredentials.addClickListener(event -> {
            updateUser();
        });

        VerticalLayout vlTextFields = new VerticalLayout(
                textFieldUsername,
                textFieldPassword,
                textFieldFirstName,
                textFieldLastName,
                textFieldEmail,
                textFieldPhone,
                buttonChangeCredentials
        );
        vlTextFields.setId("vl-text-fields");

        add(vlLabels, vlTextFields);
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

        if (textFieldFirstName.getValue() != null && !textFieldFirstName.isEmpty())
            user.setFirstName(textFieldFirstName.getValue());

        if (textFieldLastName.getValue() != null && !textFieldLastName.isEmpty())
            user.setLastName(textFieldLastName.getValue());

        if (textFieldEmail.getValue() != null && !textFieldEmail.isEmpty())
            user.setEmail(textFieldEmail.getValue());

        if (textFieldPhone.getValue() != null && !textFieldPhone.isEmpty())
            user.setPhone(textFieldPhone.getValue());

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
