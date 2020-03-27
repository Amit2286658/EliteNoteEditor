package com.me.android.noteeditor.customListener;

/**
 * Created by lenovo on 10/31/2019.
 */

public class EmptySuggestionListener {

    private static emptySuggestionListener mEmptySuggestionListener;

    public static void confirmEmptySuggestionListner(boolean value){
        mEmptySuggestionListener.onSuggestionEmpty(value);
    }

    public static void setOnEmptySuggestionListener(emptySuggestionListener emptySuggestionListener){
        mEmptySuggestionListener = emptySuggestionListener;
    }

    public interface emptySuggestionListener{
        void onSuggestionEmpty(boolean isFolderListEmpty);
    }

}
