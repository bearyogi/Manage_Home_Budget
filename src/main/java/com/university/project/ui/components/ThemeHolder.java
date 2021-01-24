package com.university.project.ui.components;

public class ThemeHolder {
    private static final ThemeHolder themeHolder = new ThemeHolder();

    private boolean darkTheme;

    private ThemeHolder() {
        darkTheme = false;
    }

    public static ThemeHolder getInstance() {
        return themeHolder;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
}
