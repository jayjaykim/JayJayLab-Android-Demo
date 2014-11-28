package com.jayjaylab.androiddemo.app.greyhound.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.util.Constants;

import roboguice.service.RoboService;
import roboguice.util.Ln;

public class ServiceRecordingPath extends RoboService {

    final Messenger messenger = new Messenger(new IncomingHandler());
    ResultReceiver resultReceiver;

    @Override
    public void onCreate() {
        Ln.d("onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.d("onStartCommand()");
        resultReceiver = intent.getParcelableExtra("receiver");

        if(!servicesConnected()) {
            resultReceiver.send(Constants.MSG_NO_GOOGLE_SERVICE, null);
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Ln.d("onDestroy()");
        // TODO stop recording and flushes all out

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Ln.d("onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Ln.d("onBind()");
        return messenger.getBinder();
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Ln.d("servicesConnected() : Google play services is available!");
            return true;
        } else {
            Ln.d("servicesConnected() : Google play services isn't available");
            return false;
        }
    }

    protected class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Ln.d("handleMessage() : msg : %s", msg);

            switch(msg.what) {
                case Constants.MSG_START_RECORDING:
                    resultReceiver.send(Constants.MSG_ONFINISH_START_RECORDING, null);
                    break;
                case Constants.MSG_PAUSE_RECORDING:
                    resultReceiver.send(Constants.MSG_ONFINISH_PAUSE_RECORDING, null);
                    break;
                case Constants.MSG_STOP_RECORDING:
                    resultReceiver.send(Constants.MSG_ONFINISH_STOP_RECORDING, null);
                    stopSelf();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
