package com.me.android.noteeditor.contract.searchQueryDatabase;

import androidx.annotation.NonNull;

/**
 * Created by Amit on 10/18/2019.
 */

public class suggestion_model {

    String suggestionText;
    int associated_id, type, isHistory = -1, id;
    public static final int TYPE_NOTE = 1, TYPE_FOLDER = 2, ISHISTORY = 1, ISNOTHISTORY = 0, TYPE_EMPTY = 4;
    boolean isHistoryBoolean;

    public suggestion_model(String suggestionText, int associated_id, int type, int isHistory){
        this.suggestionText = suggestionText;
        this.associated_id = associated_id;
        this.type = type;
        this.isHistory = isHistory;
    }

    public suggestion_model(int id, String suggestionText, int associated_id, int type, int isHistory){
        this.id = id;
        this.suggestionText = suggestionText;
        this.associated_id = associated_id;
        this.type = type;
        this.isHistory = isHistory;
    }


    public suggestion_model(String suggestionText, int associated_id, int type, boolean isHistory){
        this.suggestionText = suggestionText;
        this.associated_id = associated_id;
        this.type = type;
        this.isHistoryBoolean = isHistory;
    }

    public int getId() {
        return id;
    }

    public int getAssociated_id() {
        return associated_id;
    }

    public String getSuggestionText() {
        return suggestionText;
    }

    public int getType() {
        return type;
    }

    @NonNull
    public boolean getHistory() {
        return this.isHistory == ISHISTORY || this.isHistory != ISNOTHISTORY && isHistoryBoolean;
    }
}
