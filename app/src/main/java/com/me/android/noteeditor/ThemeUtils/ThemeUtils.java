package com.me.android.noteeditor.ThemeUtils;

import android.app.Activity;
import android.content.Intent;

public class ThemeUtils {

    public static final int BASE_THEME = 1;
    public static final int LIGHT_THEME = 2;
    public static final int DARK_THEME = 3;

    public static void RecreateActivity(Activity activity){
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
}
