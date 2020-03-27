package com.me.android.noteeditor.customListener;

public class wrapContent_settingListener {

    private static wrapSettingListener mWrapSettingListener;

    public static void confirmWrapSetting(boolean confirmWrapSetting){
        mWrapSettingListener.onWrapContentEnabled(confirmWrapSetting);
    }

    public void setOnWrapContentSettingListener(wrapSettingListener wrapContentSettingListener){
        mWrapSettingListener = wrapContentSettingListener;
    }

    public interface wrapSettingListener{
        void onWrapContentEnabled(boolean wrapContentConfirmation);
    }
}
