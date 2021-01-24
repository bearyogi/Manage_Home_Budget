package com.university.project.ui.components;

import com.university.project.ui.views.MainView;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MainViewBus {
    private MainView mainView;

    public MainViewBus() {
    }

    public MainView getMainView() {
        return this.mainView;
    }
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }
}