package com.university.project.ui.views;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.Family;
import com.university.project.backend.entity.User;
import com.university.project.backend.service.FamilyService;
import com.university.project.ui.components.MainViewBus;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
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
    private final User activeUser = VaadinSession.getCurrent().getAttribute(User.class);
    private final MainViewBus mainViewBus;

    private final Grid<Family> gridFamilies = new Grid<>(Family.class);

    public FamiliesView(FamilyService familyService, MainViewBus mainViewBus) {
        this.familyService = familyService;
        this.mainViewBus = mainViewBus;
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

        Button buttonDeleteFamily = new Button("Usuń grupę", new Icon(VaadinIcon.CLOSE));
        buttonDeleteFamily.setIconAfterText(true);
        buttonDeleteFamily.addClickListener(click -> {
           tryDeleteFamily();
        });

        add(new HorizontalLayout(buttonAddFamily, buttonJoinToFamily, buttonDeleteFamily));
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
                familyToSave.setOwnerId(activeUser.getId());

                familyService.save(familyToSave);
                fetchAllGroups();
                updateCBInMainView();
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

    private void fetchAllGroups() {
        List<Family> families = familyService.getAll();
        gridFamilies.setItems(families);
    }

    private void updateCBInMainView() {
        mainViewBus.getMainView().updateCB();
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
                updateCBInMainView();
                Notification.show("Pomyślnie dodano do grupy!", 5000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Musisz zaznaczyć grupę do której chcesz dołączyć!", 5000, Notification.Position.MIDDLE);
        }
    }

    private void tryDeleteFamily() {
        var selectedFamily = gridFamilies.getSelectionModel().getFirstSelectedItem();
        if (selectedFamily.isPresent()) {
            Family selFamily = selectedFamily.get();
            if (selFamily.isUserInFamily(activeUser)) {
                if (userIsFamilyOwner(selFamily)) {
                    familyService.delete(selFamily);
                    fetchAllGroups();
                    updateCBInMainView();
                    Notification.show("Grupa pomyślnie usunięta!", 2500, Notification.Position.MIDDLE);
                } else {
                    Notification.show("Nie jesteś właścicielem grupy!", 2500, Notification.Position.MIDDLE);
                }
            } else {
                Notification.show("Nie należysz do tej grupy!", 2500, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Musisz zaznaczyć grupę, którą chcesz usunąć!", 2500, Notification.Position.MIDDLE);
        }
    }

    private boolean userIsFamilyOwner(Family selectedFamily) {
        return selectedFamily.getOwnerId().equals(activeUser.getId());
    }


    private void configureGrid() {
        gridFamilies.addClassName("families-grid");
        gridFamilies.setSizeFull();
        gridFamilies.setColumns("familyId", "familyName", "users");
        gridFamilies.getColumns().forEach(col -> col.setAutoWidth(true));
        gridFamilies.setSelectionMode(Grid.SelectionMode.SINGLE);

        add(gridFamilies);
    }
}
