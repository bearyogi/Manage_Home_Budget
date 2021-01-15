package com.university.project.ui.views;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Family;
import com.university.project.backend.entity.User;
import com.university.project.backend.service.FamilyService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@Route(value = "families", layout = MainView.class)
@PageTitle("Families")
public class FamiliesView extends VerticalLayout {
    private final FamilyService familyService;

    private final Grid<Family> gridFamilies = new Grid<>(Family.class);
    private final User activeUser = VaadinSession.getCurrent().getAttribute(User.class);

    public FamiliesView(FamilyService familyService) {
        this.familyService = familyService;
        setSizeFull();

        addButtons();
        configureGrid();
        fetchAllGroups();
    }

    private void addButtons() {
        Button buttonAddFamily = new Button("Dodaj grupę", new Icon(VaadinIcon.PLUS_CIRCLE));
        buttonAddFamily.setIconAfterText(true);
        buttonAddFamily.addClickListener(click -> {
            showAddFamilyDialog();
        });

        Button buttonJoinToFamily = new Button("Dołącz do grupy", new Icon(VaadinIcon.GROUP));
        buttonJoinToFamily.setIconAfterText(true);
        buttonJoinToFamily.addClickListener(click -> {
            tryAddUserToFamily();
        });
        add(new HorizontalLayout(buttonAddFamily, buttonJoinToFamily));
    }

    private void showAddFamilyDialog() {
        Dialog dialog = new Dialog();
        dialog.setId("dialog-add-family");
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        TextField textFamilyName = new TextField("Nazwa grupy");

        verticalLayout.add(
                new H4("Dodaj grupę"),
                textFamilyName
        );

        Button confirmButton = new Button("Potwierdź", event -> {
            if (textFamilyName.getValue().isEmpty()) {
                textFamilyName.setErrorMessage("Wpisz nazwę!");
            } else {
                Family familyToSave = new Family();
                familyToSave.setFamilyName(textFamilyName.getValue());
                familyToSave.setBudget(new Budget());
                familyToSave.addUser(activeUser);

                familyService.save(familyToSave);
                fetchAllGroups();
                dialog.close();
            }
        });

        Button cancelButton = new Button("Anuluj", event -> {
            dialog.close();
        });

        verticalLayout.add(new HorizontalLayout(confirmButton, cancelButton));
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(verticalLayout);
        dialog.open();
    }

    private void tryAddUserToFamily() {
        var selectedFamily = gridFamilies.getSelectionModel().getFirstSelectedItem();
        if (selectedFamily.isPresent()) {
            if (selectedFamily.get().isUserInFamily(activeUser)) {
                Notification.show("Już jesteś w tej grupie!", 5000, Notification.Position.MIDDLE);
            } else {
                Family familyToUpdate = selectedFamily.get();
                familyToUpdate.addUser(activeUser);
                familyService.update(familyToUpdate);
                fetchAllGroups();

                Notification.show("Pomyślnie dodano do grupy!", 5000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Musisz zaznaczyć grupę do której chcesz dołączyć!", 5000, Notification.Position.MIDDLE);
        }
    }

    private void configureGrid() {
        gridFamilies.addClassName("families-grid");
        gridFamilies.setSizeFull();
        gridFamilies.setColumns("familyId", "familyName", "users");
        gridFamilies.getColumns().forEach(col -> col.setAutoWidth(true));
        gridFamilies.setSelectionMode(Grid.SelectionMode.SINGLE);

        add(gridFamilies);
    }

    private void fetchAllGroups() {
        List<Family> families = familyService.getAll();
        gridFamilies.setItems(families);
    }
}
