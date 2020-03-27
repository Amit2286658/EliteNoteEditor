package com.me.android.noteeditor.customListener;

import android.graphics.Bitmap;

public class imagePickerListener {

    private static imageSelectedListener mImageSelectedListener;

    public static void confirmImageSelection(Bitmap image, boolean value){
        mImageSelectedListener.isImageSelected(image, value);
    }

    public static void setOnImageSelectedListener(imageSelectedListener imageSelectedListener){
        mImageSelectedListener = imageSelectedListener;
    }

    public interface imageSelectedListener{
        void isImageSelected(Bitmap image, boolean value);
    }

}
