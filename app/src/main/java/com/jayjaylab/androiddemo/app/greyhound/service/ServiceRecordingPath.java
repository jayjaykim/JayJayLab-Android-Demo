package com.jayjaylab.androiddemo.app.greyhound.service;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.activity.ActivityMain;
import com.jayjaylab.androiddemo.app.greyhound.model.Path;
import com.jayjaylab.androiddemo.app.greyhound.util.Constants;
import com.jayjaylab.androiddemo.app.greyhound.util.GPXWriter;
import com.jayjaylab.androiddemo.app.greyhound.util.PreferenceHelper;
import com.jayjaylab.androiddemo.util.AndroidHelper;

import org.joda.time.DateTime;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.service.RoboService;
import roboguice.util.Ln;

public class ServiceRecordingPath extends RoboService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
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
    private final int NOTIFICATION_ID = 10001;
    final Messenger messenger = new Messenger(new IncomingHandler());
    final String GPX_FILE_EXTENSION = ".gpx";
    String DIR_PATH;

    boolean isLocationServiceConnected;
//    boolean isRecording = false;
    com.jayjaylab.androiddemo.app.greyhound.model.Path currentPath;

    NotificationManagerCompat notificationManagerCompat;
//    ResultReceiver resultReceiver;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    @Inject GPXWriter gpxWriter;

    @Override
    public void onConnected(Bundle bundle) {
        Ln.d("onConnected() : bundle : %s", bundle);
        isLocationServiceConnected = true;

        resumeRecordingIfStoppedUnexpectedly();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Ln.d("onConnectionSuspended() : i : %d", i);

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

        notificationManagerCompat = NotificationManagerCompat.from(this);

//        File[] files = ContextCompat.getExternalFilesDirs(this, null);
//        DIR_PATH = files[0].getAbsolutePath();
        DIR_PATH = Environment.getExternalStorageDirectory() + "/jayjaylab";
        AndroidHelper.makeDirectory(DIR_PATH);

//        recordingState = RECORDING_STATE.IDLE;
        setLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

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
//            resultReceiver = intent.getParcelableExtra("receiver");
        }

        checkPrecondition();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Ln.d("onDestroy()");
        // stops recording and flushes all out
        stopRecording();

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Ln.d("onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Ln.d("onBind() : intent : %s", intent);
        if(intent != null) {
//            resultReceiver = intent.getParcelableExtra("receiver");
//            Ln.d("onBind() : resultReceiver : %s", resultReceiver);
        }

        return messenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        Ln.d("onRebind() : intent : %s", intent);
        if(intent != null) {
//            resultReceiver = intent.getParcelableExtra("receiver");
//            Ln.d("onRebind() : resultReceiver : %s", resultReceiver);
        }

        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Ln.d("onUnbind() : intent : %s", intent);
//        resultReceiver = null;

        super.onUnbind(intent);
        return true;
    }

    protected void resumeRecordingIfStoppedUnexpectedly() {
        Ln.d("resumeRecordingIfStoppedUnexpectedly()");
        // if in recording or paused state, recover the previous state
        switch(PreferenceHelper.getLocationRecordingState(this)) {
            case PreferenceHelper.RECORDING_STATE_RECORDING:
                Ln.d("service was running but gets terminated unexpectedly");
                currentPath = new Path();
                currentPath.getPathEntity().setStartTime(PreferenceHelper.getLocationRecordingStarttime(this));
                currentPath.getPathEntity().setGpxPath(PreferenceHelper.getGpxFilePath(this));
                resumeRecording();
                break;
            case PreferenceHelper.RECORDING_STATE_PAUSED:
                Ln.d("service was running but gets terminated unexpectedly");
                // recovers the previous Path instance
                currentPath = new Path();
                currentPath.getPathEntity().setStartTime(PreferenceHelper.getLocationRecordingStarttime(this));
                currentPath.getPathEntity().setGpxPath(PreferenceHelper.getGpxFilePath(this));
                break;
            default:
                Ln.d("service was exited successfully before");
                break;
        }
    }

    protected void checkPrecondition() {
        Ln.d("checkPrecondition()");

        if(!servicesConnected()) {
//            if(resultReceiver != null)
//                resultReceiver.send(Constants.MSG_NO_GOOGLE_SERVICE, null);
            Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
            intent.putExtra("resultCode", Constants.MSG_NO_GOOGLE_SERVICE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

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

    protected void resumeRecording() {
        Ln.d("resumeRecording()");

//        isRecording = true;
        if(currentPath == null) {
            currentPath = new Path();
            currentPath.getPathEntity().setStartTime(new DateTime().toString());
            Ln.d("resumeRecording() : currentPath : %s", currentPath);
            if(gpxWriter.isFileClosed()) {
                boolean opened = gpxWriter.openFile(DIR_PATH, getFileName() + GPX_FILE_EXTENSION);
                if(!opened) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", false);

                    currentPath = null;
                    PreferenceHelper.setGpxFilePath(this, null);
                    PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_IDLE);
                    PreferenceHelper.setLocationRecordingStartTime(this, null);

                    Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
                    intent.putExtra("resultCode", Constants.MSG_ONFINISH_START_RECORDING);
                    intent.putExtra("resultData", bundle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    return;
                }
                gpxWriter.addHeader();
            }
            currentPath.getPathEntity().setGpxPath(gpxWriter.getAbsolutePath());
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, ServiceRecordingPath.this);
            PreferenceHelper.setGpxFilePath(this, gpxWriter.getAbsolutePath());
            PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_RECORDING);
            PreferenceHelper.setLocationRecordingStartTime(this, currentPath.getPathEntity().getStartTime());
//        resultReceiver.send(Constants.MSG_ONFINISH_START_RECORDING, null);
            Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
            intent.putExtra("resultCode", Constants.MSG_ONFINISH_START_RECORDING);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            if(gpxWriter.isFileClosed()) {
                File file = new File(currentPath.getPathEntity().getGpxPath());
                Ln.d("resumeRecording() : parent : %s, name : %s", file.getParent(), file.getName());
                boolean opened = gpxWriter.openFile(file.getParent(), file.getName());
                if(!opened) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", false);

                    currentPath = null;
                    PreferenceHelper.setGpxFilePath(this, null);
                    PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_IDLE);
                    PreferenceHelper.setLocationRecordingStartTime(this, null);

                    Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
                    intent.putExtra("resultCode", Constants.MSG_ONFINISH_START_RECORDING);
                    intent.putExtra("resultData", bundle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, ServiceRecordingPath.this);
                Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
                intent.putExtra("resultCode", Constants.MSG_ONFINISH_START_RECORDING);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else {
                Ln.d("resumeRecording() : resume the paused recording...");
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, ServiceRecordingPath.this);

                PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_RECORDING);
                Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
                intent.putExtra("resultCode", Constants.MSG_ONFINISH_START_RECORDING);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    protected void pauseRecording() {
        Ln.d("pauseRecording()");

//        isRecording = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, ServiceRecordingPath.this);
        PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_PAUSED);
//        resultReceiver.send(Constants.MSG_ONFINISH_PAUSE_RECORDING, null);
        Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
        intent.putExtra("resultCode", Constants.MSG_ONFINISH_PAUSE_RECORDING);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void stopRecording() {
        Ln.d("stopRecording()");

//        isRecording = false;
        PreferenceHelper.setGpxFilePath(this, null);
        PreferenceHelper.setLocationRecordingStartTime(this, null);
        PreferenceHelper.setLocationRecordingState(this, PreferenceHelper.RECORDING_STATE_STOPPED);
        if(currentPath != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, ServiceRecordingPath.this);
            googleApiClient.disconnect();
            currentPath.getPathEntity().setEndTime(new DateTime().toString());
            Ln.d("stopRecording() : currentPath : %s", currentPath);
            gpxWriter.closeFile();

            // returns an instance of Path to the client so that the client can make use of the
            // recorded path. e.g. storing it in database to show it in UI.
            Bundle bundle = new Bundle();
            bundle.putParcelable("path", currentPath);
//            resultReceiver.send(Constants.MSG_ONFINISH_STOP_RECORDING, bundle);
            Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
            intent.putExtra("resultCode", Constants.MSG_ONFINISH_STOP_RECORDING);
            intent.putExtra("resultData", bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            currentPath = null;
        }
    }

    protected void updateNotification(int contextTextStringId) {
        Ln.d("updateNotification()");

        Intent intent = new Intent(this, ActivityMain.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ActivityMain.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this).
                        setSmallIcon(R.drawable.logo_greyhound_small).
                        setContentTitle(getString(R.string.notification_title)).
                        setContentText(getString(contextTextStringId)).
                        setPriority(NotificationCompat.PRIORITY_DEFAULT).
                        setVisibility(NotificationCompat.VISIBILITY_PUBLIC).
                        setWhen(System.currentTimeMillis()).
                        setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    protected void dismissNotification() {
        Ln.d("dismissNotification()");
        notificationManagerCompat.cancel(NOTIFICATION_ID);
    }

    protected int getRecordingState() {
        return PreferenceHelper.getLocationRecordingState(this);
    }

    protected class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
//            Ln.d("handleMessage() : msg : %s, resultReceiver : %s", msg, resultReceiver);

            switch(msg.what) {
                case Constants.MSG_START_RECORDING:
                    updateNotification(R.string.recording_path);
                    resumeRecording();
                    break;
                case Constants.MSG_PAUSE_RECORDING:
                    // displays pause message
                    updateNotification(R.string.pause_recording_path);
                    pauseRecording();
                    break;
                case Constants.MSG_STOP_RECORDING:
                    dismissNotification();
                    stopRecording();
                    stopSelf();
                    break;
                case Constants.MSG_ASK_RECORDING_STATUS:
//                    if(resultReceiver != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("recordingState", getRecordingState());
//                        resultReceiver.send(Constants.MSG_ONFINISH_ASK_RECORDING_STATUS, bundle);
                    Intent intent = new Intent(Constants.INTENT_FILTER_TAG);
                    intent.putExtra("resultCode", Constants.MSG_ONFINISH_ASK_RECORDING_STATUS);
                    intent.putExtra("resultData", bundle);
                    LocalBroadcastManager.getInstance(ServiceRecordingPath.this).sendBroadcast(intent);
//                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
