package com.me.android.noteeditor.customListener;

public class EmptyData_listener {

    private static EmptyDataListener mEmptyDataListener;

    public static void confirmDataEmpty(boolean isDataEmpty){
        mEmptyDataListener.onDataEmpty(isDataEmpty);
    }

    public static void setOnDataEmptyListener(EmptyDataListener emptyListener){
        mEmptyDataListener = emptyListener;
    }

    public interface EmptyDataListener {
        void onDataEmpty(boolean isDataEmpty);
    }
}
