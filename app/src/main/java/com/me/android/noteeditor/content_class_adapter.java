package com.me.android.noteeditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.me.android.noteeditor.fontManager.fontManager;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//TODO : delete this class as it has been deprecated now, and app now uses the recyclerView instead of listView.
@Deprecated
public class content_class_adapter extends ArrayAdapter<String> {

    private static Typeface typeface;

    private SharedPreferences sharedPrefs;

    public content_class_adapter(@NonNull Context context, ArrayList<String> datalist) {
        super(context, 0, datalist);
    }

    static class ViewHolder{
        ViewHolder(){
            /////empty constructor
        }
        TextView Title, Time;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        fontManager fontManager = new fontManager(getContext());
        fontManager.initializeFontManager();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String fonttype = sharedPrefs.getString(
                "font_preference_key",
                "Aller_Lt.ttf"
        );

        View ListItemView = convertView;
        if (ListItemView == null){
            ListItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_class_item_layout, parent, false);

            holder = new ViewHolder();

            holder.Title = (TextView)ListItemView.findViewById(R.id.listViewTitle);
            holder.Time = (TextView)ListItemView.findViewById(R.id.listViewTime);

            ListItemView.setTag(holder);
        }
        else holder = (ViewHolder) ListItemView.getTag();

        String getPosition = getItem(position);
        String[] StringArray = getPosition.split("%divider");

        String pathToFont = null;
        try {
            pathToFont = fontManager.getFontPath(fonttype);
        } catch (IOException e) {
            e.printStackTrace();
        }
        typeface = Typeface.createFromAsset(getContext().getAssets(), pathToFont);

        holder.Title.setText(StringArray[0]);
        holder.Title.setTypeface(typeface);
        holder.Time.setText(StringArray[1]);
        /*holder.Time.setTextColor(fetchAccentColor());*/

        return ListItemView;
    }
    /*private int fetchAccentColor(){
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int colorAccent = typedArray.getColor(0, ContextCompat.getColor(getContext(), R.color.colorAccent));
        typedArray.recycle();

        return colorAccent;
    }*/
}
