package com.me.android.noteeditor.customSettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.me.android.noteeditor.R;

/*import android.support.v4.content.ContextCompat;*/

public class textSizeSelector extends DialogPreference {

    private int mTextSize;
    private SeekBar seekBar;
    TextView title;

    public textSizeSelector(Context context) {
        this(context, null);
    }
    public textSizeSelector(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }
    public textSizeSelector(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public textSizeSelector(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        /*setLayoutResource(R.layout.textsize);*/
    }

    public int getmTextSize() {
        return mTextSize;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        //Save into the SharedPreferences
        persistInt(mTextSize);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setmTextSize(
                restorePersistedValue ? getPersistedInt(mTextSize) : (int)defaultValue
        );
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View parentView = layoutInflater.inflate(R.layout.textsize, parent, false);
        seekBar = (SeekBar)parentView.findViewById(R.id.edit);
        seekBar.setMax(8);
        seekBar.setProgress(mTextSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekbarProgressOnStop = seekBar.getProgress();
                setmTextSize(seekbarProgressOnStop);
            }
        });

        /*we had to use the string directly instead of pointing towards the String resource, coz the getString method
        * isn't allowed here.*/

        /*int themeNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(
                "custom_theme_change_key", 1
        );

        title = (TextView) parentView.findViewById(R.id.preferenceTextSize_title);
        switch (themeNumber) {
            case 1 :
                title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                break;
            case 2 :
                title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                break;
            case 3 :
                title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                break;
        }*/

        /*ThemeChangeListener themeChangeListenerInJava = new ThemeChangeListener();
        themeChangeListenerInJava.setOnThemeChangeListener(new ThemeChangeListener.themeChangeListenerInJava() {
            @Override
            public void isThemeChanged(boolean isThemeChanged, int theme_identifier) {
                if (isThemeChanged){
                    switch (theme_identifier){
                        case 1 :
                            title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                            break;
                        case 2 :
                            title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                            break;
                        case 3 :
                            title.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                            break;
                    }
                }
            }
        });*/

        return parentView;
    }

    /*@Override
    public int getDialogLayoutResource() {
        Toast.makeText(getContext(), "getDialogLayoutResource Called", Toast.LENGTH_SHORT).show();
        return R.layout.textsize;
    }*/
}
