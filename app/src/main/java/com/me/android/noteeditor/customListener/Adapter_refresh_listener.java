package com.me.android.noteeditor.customListener;

public class Adapter_refresh_listener {

    private static AdapterRefreshListener mAdapterRefreshListener;

    public static void confirmAdapterRefresh(boolean shouldAdapterRefresh){
        mAdapterRefreshListener.shouldRefreshAdapter(shouldAdapterRefresh);
    }

    public static void setOnAdapterRefreshListener(AdapterRefreshListener mAdapterRefreshListener) {
        Adapter_refresh_listener.mAdapterRefreshListener = mAdapterRefreshListener;
    }

    public interface AdapterRefreshListener{
        void shouldRefreshAdapter(boolean should_adapter_refresh);
    }

}
