package com.me.android.noteeditor.customSettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.me.android.noteeditor.R;

@Deprecated
public class colorPicker extends DialogPreference {

    private int mColor;

    public colorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public colorPicker(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.dialogPreferenceStyle);
    }


    public void setmColor(int mColor) {
        this.mColor = mColor;
        persistInt(mColor);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0xFF000000);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setmColor(
                restorePersistedValue ? getPersistedInt(mColor) : (int) defaultValue
        );
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            setmColor(mColor);
            Toast.makeText(getContext(), "This feature is not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    /*method that gets called whenever the settings has to display a dialog when a preference is clicked*/
    @Override
    protected View onCreateDialogView() {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parentview = layoutInflater.inflate(R.layout.color_picker, null, false);

        /*ColorPickerView colorPickerView =
                (ColorPickerView)parentview.findViewById(R.id.color_picker);*/

        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int theme = prefs.getInt(
                "custom_theme_change_key", 1
        );

        switch (theme){
            case 1 :
                if (mColor == 0xFF000000)
                    colorPickerView.setColor(Color.parseColor("#F0514B"));
                else colorPickerView.setColor(mColor);
                break;
            case 2 :
                if (mColor == 0xFF000000)
                    *//*colorPickerView.setColor();*//*
        }*/
        /*colorPickerView.setColor(mColor);

            colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int i) {
                    mColor = i;
                }
            });*/

        return parentview;
    }
}
