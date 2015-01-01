package com.jayjaylab.androiddemo.app.greyhound.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.event.OnReceiveResultEvent;
import com.jayjaylab.androiddemo.app.greyhound.fragment.FragmentPathHistory;

import roboguice.event.EventManager;
import roboguice.util.Ln;

/**
 * Created by jongjoo on 1/1/15.
 */

@Singleton
public class MyResultReceiver {
    @Inject EventManager eventManager;

    @SuppressWarnings("unused")
    public ResultReceiver getResultReceiver() {
        return resultReceiver;
    }

    ResultReceiver resultReceiver = new ResultReceiver(null) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Ln.d("onReceiveResult() : resultCode : %d, resultData : %s, resultReceiver : %s",
                    resultCode, resultData, resultReceiver);
            eventManager.fire(new OnReceiveResultEvent(resultCode, resultData));
        }
    };
}
