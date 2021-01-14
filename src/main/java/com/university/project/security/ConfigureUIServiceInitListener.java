package com.university.project.security;

import com.university.project.backend.entity.User;
import com.university.project.ui.views.LoginView;
import com.university.project.ui.views.RegisterView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget())
                && !RegisterView.class.equals(event.getNavigationTarget())
                && VaadinSession.getCurrent().getAttribute(User.class) == null) {
            event.rerouteTo(LoginView.class);
        }
    }
}