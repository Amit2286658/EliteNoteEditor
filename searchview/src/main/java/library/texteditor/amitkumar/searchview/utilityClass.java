package library.texteditor.amitkumar.searchview;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Amit on 11/20/2019.
 */

public class utilityClass {

    private static final String RES_NAME = "status_bar_height", DEF_TYPE = "dimen", DEF_PACKAGE = "android",
            NAV_BAR_NAME = "config_showNavigationBar", NAV_BAR_DEF_TYPE = "bool", NAV_BAR_HEIGHT = "navigation_bar_height";

    public static final int SKIP_STATUS_BAR = 1;

    public static void fixDimension(Context context, View view, int argument){
        /*int resId = ((Activity)context).getResources().getIdentifier(RES_NAME, DEF_TYPE, DEF_PACKAGE);
        if (resId > 0){
            int systemStatusBarHeight = context.getResources().getDimensionPixelOffset(resId);*/
        int systemStatusBarHeight = getStatusBarHeight(context);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        if (argument == SKIP_STATUS_BAR) {
            params.setMargins(0, 0, 0, getNavBarHeight(context));
            view.setLayoutParams(params);
        }else {
            params.setMargins(0, systemStatusBarHeight, 0, getNavBarHeight(context));
            view.setLayoutParams(params);
        }
        //}
    }

    public static boolean checkHasNavBar(Context context){
        int resId = context.getResources().getIdentifier(NAV_BAR_NAME, NAV_BAR_DEF_TYPE, DEF_PACKAGE);
        return resId > 0 && context.getResources().getBoolean(resId);
    }

    public static int getNavBarHeight(Context context){
        if (checkHasNavBar(context)){
            int resId = context.getResources().getIdentifier(NAV_BAR_HEIGHT, DEF_TYPE, DEF_PACKAGE);
            if (resId > 0){
                return context.getResources().getDimensionPixelOffset(resId);
            }return 0;
        }else return 0;
    }

    public static int getStatusBarHeight(Context context){
        int height = 0;
        int resId = context.getResources().getIdentifier(RES_NAME, DEF_TYPE, DEF_PACKAGE);
        if (resId > 0){
            height = context.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

}
