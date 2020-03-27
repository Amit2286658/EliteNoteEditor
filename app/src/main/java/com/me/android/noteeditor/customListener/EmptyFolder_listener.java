package com.me.android.noteeditor.customListener;

public class EmptyFolder_listener {

    private static EmptyFolderListener mEmptyFolderListener;

    public static void confirmEmptyLFolderListner(boolean value){
        mEmptyFolderListener.onFolderEmpty(value);
    }

    public static void setOnEmptyFolderListener(EmptyFolderListener emptyFolderListener){
        mEmptyFolderListener = emptyFolderListener;
    }

    public interface EmptyFolderListener{
        void onFolderEmpty(boolean isFolderListEmpty);
    }

}
