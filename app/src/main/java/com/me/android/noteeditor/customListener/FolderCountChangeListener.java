package com.me.android.noteeditor.customListener;

public class FolderCountChangeListener {

    private static FolderCountChange_listener mFolderCountChangelistener;

    public static void confirmFolderCountChangeListner(boolean value){
        mFolderCountChangelistener.onFolderCountChange(value);
    }

    public static void setOnFolderCountChangeListener(FolderCountChange_listener folderCountChangeListener){
        mFolderCountChangelistener = folderCountChangeListener;
    }

    public interface FolderCountChange_listener{
        void onFolderCountChange(boolean value);
    }

}
