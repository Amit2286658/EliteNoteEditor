package com.me.android.noteeditor.IMM;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.me.android.noteeditor.IMM.IMMResult;

public class IMMHandler {

    public static InputMethodManager getIMManager(Context context){
        return (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static IMMResult getIMMResult (){
        return new IMMResult();
    }
}
