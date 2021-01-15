package com.university.project.ui.views;

import com.university.project.backend.entity.Family;
import com.university.project.backend.entity.User;
import com.university.project.backend.service.FamilyService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@Route(value = "myFamilies", layout = MainView.class)
@PageTitle("MyFamilies")
public class MyFamiliesView extends HorizontalLayout {
    private final FamilyService familyService;
    private final User activeUser = VaadinSession.getCurrent().getAttribute(User.class);

    private final Grid<Family> gridFamilies = new Grid<>(Family.class);

    public MyFamiliesView(FamilyService familyService) {
        addClassName("my-families-layout");
        this.familyService = familyService;
        setSizeFull();

        configureGrid();
        fetchAllGroupsByUser();
    }


    private void configureGrid() {
        gridFamilies.addClassName("families-grid");
        gridFamilies.setSizeFull();
        gridFamilies.setColumns("familyId", "familyName", "users");
        gridFamilies.getColumns().forEach(col -> col.setAutoWidth(true));
        gridFamilies.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridFamilies.getSelectionModel().addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::viewFamilyBudget));

        add(gridFamilies);
    }

    private void fetchAllGroupsByUser() {
        List<Family> families = familyService.getAllByUser(activeUser);
        gridFamilies.setItems(families);
    }

    private void viewFamilyBudget(Family selectedFamily) {
        UI.getCurrent().navigate(FamilyBudgetView.class, selectedFamily.getFamilyId());
    }
}
