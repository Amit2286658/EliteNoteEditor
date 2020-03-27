package com.me.android.noteeditor.customSettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.android.noteeditor.R;
import com.me.android.noteeditor.ThemeUtils.ThemeChangeListener;

public class themeSelector extends Preference {

    final int BASE_THEME = 1;
    final int LIGHT_THEME = 2;
    final int DARK_THEME = 3;

    private int mThemeSelection;

    public themeSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public themeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public themeSelector(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public themeSelector(Context context) {
        super(context);
    }

    public void setmThemeSelection(int mThemeSelection) {
        this.mThemeSelection = mThemeSelection;
        //Saving value into shared preferences
        persistInt(mThemeSelection);
    }

    public int getmThemeSelection() {
        return mThemeSelection;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setmThemeSelection(
                restorePersistedValue ? getPersistedInt(mThemeSelection) : (int) defaultValue
        );
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View parentView = layoutInflater.inflate(R.layout.theme_selector, parent, false);
        parentView.setClickable(false);

        final ImageView baseThemeCheckImage = (ImageView)parentView.findViewById(R.id.BaseThemeCheckImage);
        final ImageView DarkThemeImagecheck = (ImageView)parentView.findViewById(R.id.DarkThemeCheckImage);
        final ImageView LightThemeCheckImage = (ImageView)parentView.findViewById(R.id.LightthemeCheckImage);
        final TextView preferenceTitle = (TextView)parentView.findViewById(R.id.preferenceThemeSelector_title);

        /*we had to use the string directly instead of pointing towards the String resource, coz the getString method
         * isn't allowed here.*/

        /*int themeNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(
                "custom_theme_change_key", 1
        );
        switch (themeNumber) {
            case 1 :
                preferenceTitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                break;
            case 2 :
                preferenceTitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                break;
            case 3 :
                preferenceTitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                break;
        }*/


        baseThemeCheckImage.setVisibility(View.INVISIBLE);
        DarkThemeImagecheck.setVisibility(View.INVISIBLE);
        LightThemeCheckImage.setVisibility(View.INVISIBLE);

        switch (getPersistedInt(mThemeSelection)){
            case 1:
                baseThemeCheckImage.setVisibility(View.VISIBLE);
                break;
            case 2:
                LightThemeCheckImage.setVisibility(View.VISIBLE);
                break;
            case 3:
                DarkThemeImagecheck.setVisibility(View.VISIBLE);
                break;
                default: break;
        }

        FrameLayout baseTheme = (FrameLayout) parentView.findViewById(R.id.BaseThemeCheckFrameLayout);
        baseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmThemeSelection(BASE_THEME);
                baseThemeCheckImage.setVisibility(View.VISIBLE);
                DarkThemeImagecheck.setVisibility(View.INVISIBLE);
                LightThemeCheckImage.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, BASE_THEME);
            }
        });
        FrameLayout lightTheme = (FrameLayout) parentView.findViewById(R.id.LightThemeCheckFrameLayout);
        lightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmThemeSelection(LIGHT_THEME);
                LightThemeCheckImage.setVisibility(View.VISIBLE);
                baseThemeCheckImage.setVisibility(View.INVISIBLE);
                DarkThemeImagecheck.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, LIGHT_THEME);
            }
        });
        FrameLayout darkTheme = (FrameLayout) parentView.findViewById(R.id.DarkThemeCheckFrameLayout);
        darkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmThemeSelection(DARK_THEME);
                DarkThemeImagecheck.setVisibility(View.VISIBLE);
                baseThemeCheckImage.setVisibility(View.INVISIBLE);
                LightThemeCheckImage.setVisibility(View.INVISIBLE);

                ThemeChangeListener.confirmThemeChange(true, DARK_THEME);
            }
        });

        return parentView;
    }
}
