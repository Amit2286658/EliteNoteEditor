package com.me.android.noteeditor.CommonUtility;

import android.content.Context;

import com.me.android.noteeditor.contract.searchQueryDatabase.searchQueryDatabaseManager;
import com.me.android.noteeditor.contract.searchQueryDatabase.suggestion_model;

/**
 * Created by lenovo on 10/25/2019.
 */

public class suggestionUtility {

    public suggestionUtility(){
        //empty
    }

    public static void setSuggestionHitoryData(Context context,String str){
        if (context == null) throw new IllegalArgumentException("please provide a valid context");

        searchQueryDatabaseManager sqdbManager = new searchQueryDatabaseManager(context);

        if (!str.isEmpty()){
            boolean filenameExists = false;
            for (suggestion_model title : sqdbManager.getAll()){
                if (title.getSuggestionText().equals(str)){
                    filenameExists = true;
                }
            }
            if (!filenameExists)
                sqdbManager.insertQueryEntry(str, -1, suggestion_model.TYPE_NOTE, suggestion_model.ISHISTORY);
        }

        sqdbManager = null;
    }

}
