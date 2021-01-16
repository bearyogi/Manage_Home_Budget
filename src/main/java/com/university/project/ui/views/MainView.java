package com.university.project.ui.views;

import com.university.project.backend.entity.Family;
import com.university.project.backend.service.AuthService;
import com.university.project.backend.service.FamilyService;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Optional;


/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "PersonalBudget", shortName = "Budget", enableInstallPrompt = false)
public class MainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private final AuthService authService;
    private final FamilyService familyService;
    private final ToggleButton toggleButton = new ToggleButton("Ciemny tryb", click -> {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK))
            themeList.remove(Lumo.DARK);
        else
            themeList.add(Lumo.DARK);
    });

    public MainView(AuthService authService, FamilyService familyService) {
        this.familyService = familyService;
        this.authService = authService;
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);

        Image avatar = new Image("images/user.svg", "Avatar");

        ContextMenu contextMenu = new ContextMenu(avatar);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Settings",
                e -> Notification.show("Not implemented yet.", 3000,
                        Notification.Position.BOTTOM_CENTER));
        contextMenu.addItem("Log Out",
                e -> {
                    logout();
                });

        layout.add(avatar);
        layout.add(toggleButton);
        return layout;
    }

    private void logout() {
        UI.getCurrent().getPage().setLocation("login");
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logo.png", "ManageBudget logo"));
        logoLayout.add(new H1("ManageBudget"));
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        ComboBox<Family> cb = new ComboBox("Wybierz grupę");
        cb.setItems(familyService.getAllByUser(AuthService.getUser()));
        cb.setItemLabelGenerator(family -> family.getFamilyName());
        cb.addValueChangeListener(e -> viewFamilyBudget(e.getValue().getFamilyId()));
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        tabs.add(cb);
        return tabs;
    }

    private void viewFamilyBudget(int selectedFamily) {
        UI.getCurrent().navigate(FamilyBudgetView.class, selectedFamily);
   }

    private Component[] createMenuItems() {
        return authService.getAuthorizedRoutes().stream()
                .map(r -> createTab(r.getName(), r.getView()))
                .toArray(Component[]::new);
    }


    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
