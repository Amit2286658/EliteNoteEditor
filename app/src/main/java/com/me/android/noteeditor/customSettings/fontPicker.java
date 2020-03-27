package com.me.android.noteeditor.customSettings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.me.android.noteeditor.R;
import com.me.android.noteeditor.fontManager.fontManager;

import java.io.IOException;
import java.util.ArrayList;

/*import android.support.v4.content.ContextCompat;*/

public class fontPicker extends Preference {

    private String mFont;

    public fontPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public fontPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public fontPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public fontPicker(Context context) {
        super(context);
    }

    private void setmFont(String mFont) {
        this.mFont = mFont;
        persistString(mFont);
    }

    public String getmFont() {
        return mFont;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setmFont(
                restorePersistedValue ? getPersistedString(mFont) : (String) defaultValue
        );
    }



    @Override
    protected View onCreateView(final ViewGroup parent) {
        super.onCreateView(parent);

        final fontManager fontManager = new fontManager(getContext());
        fontManager.initializeFontManager();

        final View parentView = LayoutInflater.from(getContext()).inflate(R.layout.font_picker, parent, false);

        /*TextView fontPreferenceTitle = (TextView)parentView.findViewById(R.id.fontSelectionTitle);
        fontPreferenceTitle.setTextColor(
                PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(
                        "custom_theme_change_key", 1
                ) == 3 ? ContextCompat.getColor(getContext(), android.R.color.white)
                        : ContextCompat.getColor(getContext(), android.R.color.black)
        );*/

        final Spinner spinner = (Spinner)parentView.findViewById(R.id.fontSelectionSpinner);

        //the below codes will be executed and once the fetching is completed in the background
        //it will be posted back to the spinner
        final ArrayList<String> arrayList1 = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.spinner_item_1, arrayList1);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                /*((TextView)view).setTextColor(ContextCompat.getColor(getContext(), R.color.color_material_grey));
                ((TextView)view).setTextSize(14);*/

                String font = (String) adapterView.getItemAtPosition(i);
                setmFont(font);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });

        //the below code will fetch the font names list in the background
        //and will post it to the spinner in the same background thread
        spinner.post(new Runnable() {
            @Override
            public void run() {
                if (!arrayList1.isEmpty()) arrayList1.clear();
                if (!arrayAdapter.isEmpty()) arrayAdapter.clear();
                try {
                    for (String str : fontManager.listAllFileNames()) {
                        if (str.endsWith(".ttf")){
                            arrayAdapter.add(str.replace(".ttf", ""));
                        }else if (str.endsWith(".otf")){
                            arrayAdapter.add(str.replace(".otf", ""));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                spinner.setAdapter(arrayAdapter);
                spinner.setSelection(arrayAdapter.getPosition(mFont));
            }
        });

        /*TextView spinnerTextView = (TextView)spinner.findViewById(R.id.spinner_item_custom_text);

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(
                "custom_theme_change_key", 1
        ) == 3) {
            spinnerTextView.setCompoundDrawables
                    (null, null,
                            ContextCompat.getDrawable
                                    (getContext(), R.drawable.ic_arrow_drop_down_circle_white_24dp), null);
        } else {
            spinnerTextView.setCompoundDrawables
                    (null, null,
                            ContextCompat.getDrawable
                                    (getContext(), R.drawable.ic_arrow_drop_down_circle_black_24dp), null);
        }*/

        /*Object[] objects = {fontManager, parentView, getContext()};
        loadInBakground loadInBakground = new loadInBakground();
        loadInBakground.execute(objects);*/

        return parentView;
    }

    static class loadInBakground extends AsyncTask<Object, String, ArrayList<String>> {

        Object[] objects;

        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            this.objects = objects;
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                arrayList.addAll(((fontManager)this.objects[0]).listAllFileNames());
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            View parentView = (View) this.objects[1];
            Spinner spinner = (Spinner)parentView.findViewById(R.id.fontSelectionSpinner);
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<>((Context) this.objects[2], android.R.layout.simple_spinner_dropdown_item, strings);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(arrayAdapter.getPosition(new fontPicker((Context)objects[2]).getmFont()));
        }
    }

}
