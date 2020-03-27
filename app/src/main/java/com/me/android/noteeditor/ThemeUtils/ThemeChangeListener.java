package com.me.android.noteeditor.ThemeUtils;

public class ThemeChangeListener {

    private static themeChangeListener mThemeChangeListener;

    public static void confirmThemeChange(boolean themeChanged, int themeIdentifier){
        mThemeChangeListener.isThemeChanged(themeChanged, themeIdentifier);
    }

    public static void setOnThemeChangeListener(themeChangeListener themeChangeListener){
        mThemeChangeListener = themeChangeListener;
    }

    public interface themeChangeListener{
        void isThemeChanged(boolean isThemeChanged, int theme_identifier);
    }
}
