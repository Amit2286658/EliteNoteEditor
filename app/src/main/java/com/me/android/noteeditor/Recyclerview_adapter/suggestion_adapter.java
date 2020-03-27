package com.me.android.noteeditor.Recyclerview_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.me.android.noteeditor.CommonUtility.searchViewUtility;
import com.me.android.noteeditor.CommonUtility.suggestionUtility;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.contract.searchQueryDatabase.searchQueryDatabaseManager;
import com.me.android.noteeditor.contract.searchQueryDatabase.suggestion_model;
import com.me.android.noteeditor.customListener.EmptySuggestionListener;
import com.me.android.noteeditor.fragment_container;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.me.android.noteeditor.CommonUtility.utilityClass.getDipValue;

/**
 * Created by Amit on 10/18/2019.
 */

public class suggestion_adapter extends RecyclerView.Adapter<suggestionViewHolder> {

    private ArrayList<suggestion_model> suggestion_models = new ArrayList<>();
    private EditText editText;

    /*private DataBaseManager dbManager;
    private FoldersDataBaseManager fdbManager;*/

    private PopupWindow mPopupWindow;

    private int default_material_color;

    private searchQueryDatabaseManager sqdbManager;

    public suggestion_adapter(Context context, ArrayList<suggestion_model> list, EditText editText, int color){
        this.suggestion_models = list;
        /*dbManager = new DataBaseManager(context);
        fdbManager = new FoldersDataBaseManager(context);*/
        this.editText = editText;
        this.default_material_color = color;
        this.sqdbManager = new searchQueryDatabaseManager(context);
        context = null;
    }

    @Override
    public suggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View suggestion_view = layoutInflater.inflate(R.layout.suggestion_layout, parent, false);
        return new suggestionViewHolder(suggestion_view);
    }

    @Override
    public void onBindViewHolder(final suggestionViewHolder holder, final int position) {
        holder.searchIcon.setImageDrawable(null);
        holder.clearIcon.setVisibility(View.GONE);
        if (suggestion_models.get(position).getHistory()){
            holder.searchIcon.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_history_black_24dp));
        }else{
            holder.searchIcon.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_new_search_black_24dp));
        }
        holder.fillIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bufferText = holder.suggestion.getText().toString();
                editText.setText(bufferText);
            }
        });
        holder.clearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQueryDatabaseManager sqdbManager = new searchQueryDatabaseManager(holder.context);
                if (suggestion_models.get(position).getHistory()){
                    sqdbManager.deleteQueryEntry(suggestion_models.get(position).getAssociated_id());
                }
            }
        });

        if(suggestion_models.get(position).getType() == suggestion_model.TYPE_EMPTY){
            holder.fillIcon.setVisibility(View.GONE);
            holder.searchIcon.setVisibility(View.GONE);
        }

        holder.suggestion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (suggestion_models.get(position).getHistory()){

                    TextView textView = (TextView) ((LayoutInflater)holder.context.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE)).inflate(R.layout.suggestion_popup_window, null);

                    mPopupWindow = new PopupWindow(
                            textView,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            getDipValue(holder.context, 42)
                    );
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setBackgroundDrawable(new ColorDrawable(default_material_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPopupWindow.setElevation(getDipValue(holder.context, 6));
                    }
                    mPopupWindow.showAsDropDown(v);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                sqdbManager.deleteQueryEntry(suggestion_models.get(position).getId());
                                refresh();
                                if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                                if (suggestion_models.isEmpty()) {
                                    EmptySuggestionListener.confirmEmptySuggestionListner(true);
                                }
                            }catch (IndexOutOfBoundsException e){
                                //empty
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });


        holder.suggestion.setText(suggestion_models.get(position).getSuggestionText());
        holder.suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                suggestionUtility.setSuggestionHitoryData(holder.context, suggestion_models.get(position).getSuggestionText());

                Intent intent = new Intent();
                intent.putExtra(holder.context.getString(R.string.from_folder_to_fragment_container_intent_key),
                        suggestion_models.get(position).getSuggestionText());
                intent.putExtra(holder.context.getString(R.string.from_folder_to_fragment_container_identify_key), 2);
                intent.setClass(holder.context, fragment_container.class);
                holder.context.startActivity(intent);
                if (searchViewUtility.isSearchViewAdded()){
                    searchViewUtility.onBackPressed(holder.context, false);
                }

                /*boolean contains = false;

                for (suggestion_model item : sqdbManager.getAll()){
                    if (item.getSuggestionText().equals(suggestion_models.get(position).getSuggestionText())){
                        contains = true;
                    }
                }
                if (!contains) {
                    if (suggestion_models.get(position).getType() == suggestion_model.TYPE_NOTE) {
                        content_class dataItem = dbManager.getSingleData(suggestion_models.get(position).getAssociated_id());
                        sqdbManager.insertQueryEntry(suggestion_models.get(position).getSuggestionText(),
                                dataItem.getId(), suggestion_model.TYPE_NOTE, suggestion_model.ISHISTORY);
                    }
                    if (suggestion_models.get(position).getType() == suggestion_model.TYPE_FOLDER){
                        Folder_content_class dataItem = fdbManager.getSingleData(
                                suggestion_models.get(position).getAssociated_id());
                        sqdbManager.insertQueryEntry(suggestion_models.get(position).getSuggestionText(),
                                dataItem.getId(), suggestion_model.TYPE_FOLDER, suggestion_model.ISHISTORY);
                    }
                }

                if (suggestion_models.get(position).getType() == suggestion_model.TYPE_NOTE
                        && !suggestion_models.get(position).getHistory()){
                    content_class dataItem = dbManager.getSingleData(suggestion_models.get(position).getAssociated_id());
                    Intent intent = new Intent(holder.context, editingActivity.class);
                    intent.putExtra(holder.context.getString(R.string.key_value_title), dataItem.getTitle());
                    intent.putExtra(holder.context.getString(R.string.key_value_content), dataItem.getContent());
                    intent.putExtra(holder.context.getString(R.string.key_value_id), dataItem.getId());
                    intent.putExtra(holder.context.getString(R.string.key_value_time), dataItem.getTime());
                    intent.putExtra(holder.context.getString(R.string.key_value_folderName), dataItem.getFolderName());
                    holder.context.startActivity(intent);
                    Util.hideKeyboard(holder.suggestion, holder.context);
                }
                if (suggestion_models.get(position).getType() == suggestion_model.TYPE_FOLDER
                        && !suggestion_models.get(position).getHistory()){
                    Folder_content_class dataItem = fdbManager.getSingleData(suggestion_models.get(position).getAssociated_id());
                    Intent intent = new Intent(holder.context, fragment_container.class);
                    intent.putExtra(
                            holder.context.getString(R.string.from_folder_to_fragment_container_intent_key), dataItem.getFolderName());
                    holder.context.startActivity(intent);
                    Util.hideKeyboard(holder.suggestion, holder.context);
                }
                if (suggestion_models.get(position).getType() == suggestion_model.TYPE_NOTE
                        && suggestion_models.get(position).getHistory()){
                    suggestion_model suggestionItem = sqdbManager.getQueryEntry(suggestion_models.get(position).getId());
                    int associate_id = suggestionItem.getAssociated_id();
                    content_class dataItem = dbManager.getSingleData(associate_id);

                    Intent intent = new Intent(holder.context, editingActivity.class);
                    intent.putExtra(holder.context.getString(R.string.key_value_title), dataItem.getTitle());
                    intent.putExtra(holder.context.getString(R.string.key_value_content), dataItem.getContent());
                    intent.putExtra(holder.context.getString(R.string.key_value_id), dataItem.getId());
                    intent.putExtra(holder.context.getString(R.string.key_value_time), dataItem.getTime());
                    intent.putExtra(holder.context.getString(R.string.key_value_folderName), dataItem.getFolderName());
                    holder.context.startActivity(intent);
                    Util.hideKeyboard(holder.suggestion, holder.context);
                }
                if (suggestion_models.get(position).getType() == suggestion_model.TYPE_FOLDER
                        && suggestion_models.get(position).getHistory()){
                    suggestion_model suggestionItem = sqdbManager.getQueryEntry(suggestion_models.get(position).getId());
                    int associated_id = suggestionItem.getAssociated_id();

                    Folder_content_class dataItem = fdbManager.getSingleData(associated_id);

                    Intent intent = new Intent(holder.context, fragment_container.class);
                    intent.putExtra(
                            holder.context.getString(R.string.from_folder_to_fragment_container_intent_key), dataItem.getFolderName());
                    holder.context.startActivity(intent);
                    Util.hideKeyboard(holder.suggestion, holder.context);
                }*/
                /*if (suggestion_models.get(position).getType() == suggestion_model.TYPE_SEARCH){
                    editText.setText(suggestion_models.get(position).getSuggestionText());
                    *//*Intent intent = new Intent();
                    intent.setClass((Activity)holder.context, search_result.class);
                    intent.putExtra(holder.context.getString(R.string.search_result_intent_key)
                            , suggestion_models.get(position).getSuggestionText());*//*
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestion_models.size();
    }

    public void setData(ArrayList<suggestion_model> list){
        suggestion_models.clear();
        suggestion_models.addAll(list);
        notifyDataSetChanged();
    }

    public void clear(){
        suggestion_models.clear();
        notifyDataSetChanged();
    }

    public void refresh(){
        suggestion_models.clear();
        suggestion_models.addAll(sqdbManager.getAll());
        if (sqdbManager.getAll().isEmpty() || sqdbManager.getAll() == null)
            EmptySuggestionListener.confirmEmptySuggestionListner(true);
        notifyDataSetChanged();
    }
}

class suggestionViewHolder extends RecyclerView.ViewHolder{

    TextView suggestion;
    ImageView searchIcon, fillIcon, clearIcon;
    Context context;

    public suggestionViewHolder(View itemView) {
        super(itemView);
        suggestion = (TextView) itemView.findViewById(R.id.suggestion_text_view);
        searchIcon = (ImageView) itemView.findViewById(R.id.suggestion_icon);
        fillIcon = (ImageView) itemView.findViewById(R.id.suggestion_fill_icon);
        clearIcon = (ImageView) itemView.findViewById(R.id.suggestion_clear_icon);
        context = itemView.getContext();
    }
}
