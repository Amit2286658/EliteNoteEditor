package com.me.android.noteeditor.customSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.me.android.noteeditor.R;

public class transparencySelector extends DialogPreference {

    private int mTransparency;
    private static int seekBarProgress;

    public transparencySelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public transparencySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText(null);
        setNegativeButtonText(null);
    }

    public void setmTransparency(int mTransparency) {
        this.mTransparency = mTransparency;
        persistInt(this.mTransparency);
    }

    /**
     * Called when a Preference is being inflated and the default value
     * attribute needs to be read. Since different Preference types have
     * different value types, the subclass should get and return the default
     * value which will be its value type.
     * <p>
     * For example, if the value type is String, the body of the method would
     * proxy to {@link TypedArray#getString(int)}.
     *
     * @param a     The set of attributes.
     * @param index The index of the default value attribute.
     * @return The default value of this preference type.
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    /**
     * Implement this to set the initial value of the Preference.
     *
     * <p>If <var>restorePersistedValue</var> is true, you should restore the
     * Preference value from the {@link SharedPreferences}. If
     * <var>restorePersistedValue</var> is false, you should set the Preference
     * value to defaultValue that is given (and possibly store to SharedPreferences
     * if {@link #shouldPersist()} is true).
     *
     * <p>In case of using {@link PreferenceDataStore}, the <var>restorePersistedValue</var> is
     * always {@code true}. But the default value (if provided) is set.
     *
     * <p>This may not always be called. One example is if it should not persist
     * but there is no default value given.
     *
     * @param restorePersistedValue True to restore the persisted value;
     *                              false to use the given <var>defaultValue</var>.
     * @param defaultValue          The default value for this Preference. Only use this
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setmTransparency(
                restorePersistedValue ? getPersistedInt(mTransparency) : (int)defaultValue
        );
    }

    /**
     * Creates the content view for the dialog (if a custom content view is
     * required). By default, it inflates the dialog layout resource if it is
     * set.
     *
     * @return The content View for the dialog.
     * @see #setLayoutResource(int)
     */
    @Override
    protected View onCreateDialogView() {
        seekBarProgress = mTransparency;

        View parentView = LayoutInflater.from(getContext()).inflate(R.layout.transparency_selector, null, false);
        SeekBar transparencySeekbar = (SeekBar)parentView.findViewById(R.id.transparency_level);
        transparencySeekbar.setMax(9);
        transparencySeekbar.setProgress(mTransparency);
        transparencySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //empty
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarProgress = seekBar.getProgress();
            }
        });

        TextView okButton = (TextView)parentView.findViewById(R.id.transparency_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setmTransparency(seekBarProgress);
                getDialog().dismiss();
            }
        });
        TextView cancelButton = (TextView)parentView.findViewById(R.id.transparency_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return parentView;
    }


}
