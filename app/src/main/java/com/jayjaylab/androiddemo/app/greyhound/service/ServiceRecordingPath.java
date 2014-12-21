package com.jayjaylab.androiddemo.app.greyhound.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.model.Path;
import com.jayjaylab.androiddemo.app.greyhound.util.Constants;
import com.jayjaylab.androiddemo.app.greyhound.util.GPXWriter;
import com.jayjaylab.androiddemo.util.AndroidHelper;

import org.joda.time.DateTime;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.service.RoboService;
import roboguice.util.Ln;

public class ServiceRecordingPath extends RoboService implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    LocationListener {
    final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    final Messenger messenger = new Messenger(new IncomingHandler());
    final String GPX_FILE_EXTENSION = ".gpx";
    String DIR_PATH;

    boolean isLocationServiceConnected;
    com.jayjaylab.androiddemo.app.greyhound.model.Path currentPath;

    ResultReceiver resultReceiver;
    LocationClient locationClient;
    LocationRequest locationRequest;

    @Inject GPXWriter gpxWriter;

    @Override
    public void onConnected(Bundle bundle) {
        Ln.d("onConnected() : bundle : %s", bundle);
        isLocationServiceConnected = true;
    }

    @Override
    public void onDisconnected() {
        Ln.d("onDisconnected()");
        isLocationServiceConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Ln.d("onConnectionFailed() : connectionResult() : %s", connectionResult);
        isLocationServiceConnected = false;
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(
//                        this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                Ln.e(e);
//            }
//        } else {
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Ln.d("onLocationChanged() : location : %s", location);
        gpxWriter.write(location);
    }

    @Override
    public void onCreate() {
        Ln.d("onCreate()");

//        File[] files = ContextCompat.getExternalFilesDirs(this, null);
//        DIR_PATH = files[0].getAbsolutePath();
        DIR_PATH = Environment.getExternalStorageDirectory() + "/jayjaylab";
        AndroidHelper.makeDirectory(DIR_PATH);

//        recordingState = RECORDING_STATE.IDLE;
        setLocationRequest();
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();

        super.onCreate();
    }

    protected void setLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.d("onStartCommand()");
        if(intent != null) {
            resultReceiver = intent.getParcelableExtra("receiver");
        }

        checkPrecondition();
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
        if(intent != null) {
            resultReceiver = intent.getParcelableExtra("receiver");
        }

        return messenger.getBinder();
    }

    protected void checkPrecondition() {
        if(!servicesConnected()) {
            if(resultReceiver != null)
                resultReceiver.send(Constants.MSG_NO_GOOGLE_SERVICE, null);
            stopSelf();
        }
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

    protected String getFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void startRecording() {
        Ln.d("startRecording()");

        currentPath = new Path();
        currentPath.getPathEntity().setStartTime(new DateTime().toString());
        Ln.d("startRecording() : currentPath : %s", currentPath);
        if(gpxWriter.isFileClosed()) {
            boolean opened = gpxWriter.openFile(DIR_PATH, getFileName() + GPX_FILE_EXTENSION);
            if(!opened) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", false);
                resultReceiver.send(Constants.MSG_ONFINISH_START_RECORDING, bundle);
                return;
            }
        }
        currentPath.getPathEntity().setGpxPath(gpxWriter.getAbsolutePath());
        locationClient.requestLocationUpdates(locationRequest, ServiceRecordingPath.this);
        resultReceiver.send(Constants.MSG_ONFINISH_START_RECORDING, null);
    }

    protected void pauseRecording() {
        Ln.d("pauseRecording()");

        locationClient.removeLocationUpdates(ServiceRecordingPath.this);
        resultReceiver.send(Constants.MSG_ONFINISH_PAUSE_RECORDING, null);
    }

    protected void stopRecording() {
        Ln.d("stopRecording()");

        locationClient.removeLocationUpdates(ServiceRecordingPath.this);
        currentPath.getPathEntity().setEndTime(new DateTime().toString());
        currentPath.getPathEntity().setGpxPath(gpxWriter.getAbsolutePath());
        Ln.d("stopRecording() : currentPath : %s", currentPath);
        gpxWriter.closeFile();

        // returns an instance of Path to the client so that the client can make use of the
        // recorded path. e.g. storing it in database to show it in UI.
        Bundle bundle = new Bundle();
        bundle.putParcelable("path", currentPath);
        resultReceiver.send(Constants.MSG_ONFINISH_STOP_RECORDING, bundle);
    }

    protected class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Ln.d("handleMessage() : msg : %s", msg);

            switch(msg.what) {
                case Constants.MSG_START_RECORDING:
                    startRecording();
                    break;
                case Constants.MSG_PAUSE_RECORDING:
                    pauseRecording();
                    break;
                case Constants.MSG_STOP_RECORDING:
                    stopRecording();
                    stopSelf();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
