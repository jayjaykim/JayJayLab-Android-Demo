package com.jayjaylab.androiddemo.app.greyhound.event;

import android.os.Bundle;

/**
 * Created by jongjoo on 1/1/15.
 */
public class OnReceiveResultEvent {
    int resultCode;
    Bundle resultData;

    public OnReceiveResultEvent(int resultCode, Bundle resultData) {
        this.resultCode = resultCode;
        this.resultData = resultData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Bundle getResultData() {
        return resultData;
    }

    public void setResultData(Bundle resultData) {
        this.resultData = resultData;
    }
}
