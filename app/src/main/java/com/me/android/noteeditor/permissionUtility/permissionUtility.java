package com.me.android.noteeditor.permissionUtility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public final class permissionUtility {

    public static final int FLAG_FIRST_TIME_PERMISSION_REQUEST = 28;
    public static final int FLAG_PERMISSION_DENIED_WITH_DO_NOT_DISTURB_SETTING = 86;
    public static final int FLAG_RUNTIME_PERMISSION_DENIED_ONCE = 18;
    public static final int FLAG_RUNTIME_PERMISSION_DENIED_WITH_DO_NOT_ASK_SETTING = 58;

    private static final String PERMISSION_CHECKER_PREFERENCE = "PERMISSION_CHECKER_PREFERENCE"
            , PERMISSION_CHECKER_PREFERENCE_KEY = "permissionCheckerPreferenceKey";

    public static boolean checkAllPermissions(int... permissions){

        if (permissions.length < 1)
            return false;

        for (int each : permissions){
            if (each != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    public static boolean ShowPermissionRationale(Activity activity, String... permissions){
        SharedPreferences.Editor sp = activity.getSharedPreferences(PERMISSION_CHECKER_PREFERENCE, Context.MODE_PRIVATE).edit();

        boolean showPermissionRationale = false;

        if (permissions.length < 1) throw new IllegalArgumentException("Please provide at least one permission to check");

        for (String each : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, each)) {
                showPermissionRationale = true;
                sp.putInt(PERMISSION_CHECKER_PREFERENCE_KEY, 1)
                .apply();
            }
        }

        return showPermissionRationale;
    }

    public static int CheckPermissionFlag(Activity activity){
        SharedPreferences sp = activity.getSharedPreferences(PERMISSION_CHECKER_PREFERENCE, Context.MODE_PRIVATE);
        int getPermissionPreferenceChecker = sp.getInt(PERMISSION_CHECKER_PREFERENCE_KEY, 0);
        int updated_preferenceValue = -1;
        if (getPermissionPreferenceChecker == 0){
            updated_preferenceValue = FLAG_FIRST_TIME_PERMISSION_REQUEST;
        }else if (getPermissionPreferenceChecker == 1){
            updated_preferenceValue = FLAG_PERMISSION_DENIED_WITH_DO_NOT_DISTURB_SETTING;
        }
        return updated_preferenceValue;
    }

    public static int CheckRuntimePermission(Activity activity, String... permissions){
        int check = -1;
        for (String each : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, each))
                check = FLAG_RUNTIME_PERMISSION_DENIED_ONCE;
            else check = FLAG_RUNTIME_PERMISSION_DENIED_WITH_DO_NOT_ASK_SETTING;
        }
        return check;
    }

}
