package com.me.android.noteeditor.customListener;

//this listener will be used to dispatch the permission and related stuff to the main Activity
//right now the fragment handles this calls, and there are some bugs with fragments.
public class Open_external_file_listener {
    private static openFile mOpenFile;
    public static void confirmOpenFile(boolean b){
        mOpenFile.onOpenFIle(b);
    }
    public static void setOpenFileListener(openFile opnFileListener){
        mOpenFile = opnFileListener;
    }
    public interface openFile{
        /*
        * this interface is used to dispatch the open file event only
        * to move the permission and related stuff to the main activity from the fragment*/
        void onOpenFIle(boolean value);
    }

}
