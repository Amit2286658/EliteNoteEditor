package com.me.android.noteeditor.ThemeUtils;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

@Deprecated
//TODO : delete this class as manual coloring is not required anymore, and it's existence is redundant.
public class viewUtils  {

    private static ArrayList<View> viewList = new ArrayList<>();

    public static ArrayList<View> getAllChildViews(ViewGroup viewGroup){

        for (int i = 0; i <= viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) getAllChildViews((ViewGroup) view);

            viewList.add(view);
        }
        return viewList;
    }

    public static void applyPrimarColorOnViews(ArrayList<View> viewList, int color) throws IndexOutOfBoundsException{
        for (int i = 0; i <= viewList.size(); i++){
            View view = viewList.get(i);
            if (view instanceof Toolbar){
                view.setBackgroundColor(color);
            }
        }
    }

    public static void applyAccentColorOnViews(ArrayList<View> viewList, int color) throws IndexOutOfBoundsException{
        for (int i = 0; i <= viewList.size(); i++){
            View view = viewList.get(i);
            if (view instanceof FloatingActionButton){
                view.setBackgroundColor(color);
            }
        }
    }

}
