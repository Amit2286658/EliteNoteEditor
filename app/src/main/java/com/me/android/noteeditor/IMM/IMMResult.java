package com.me.android.noteeditor.IMM;

import android.os.Bundle;
import android.os.ResultReceiver;

public class IMMResult extends ResultReceiver {

    private static int result = -1;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public IMMResult() {
        super(null);
    }

    /**
     * Override to receive results delivered to this object.
     *
     * @param resultCode Arbitrary result code delivered by the sender, as
     *                   defined by the sender.
     * @param resultData Any additional data provided by the sender.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        result = resultCode;
        super.onReceiveResult(resultCode, resultData);
    }

    public int getResult(){
        try {
            int sleep = 0;
            while (result == -1 && sleep < 500){
                Thread.sleep(100);
                sleep += 100;
            }
        }catch (InterruptedException e){
            //empty
        }
        return result;
    }
}
