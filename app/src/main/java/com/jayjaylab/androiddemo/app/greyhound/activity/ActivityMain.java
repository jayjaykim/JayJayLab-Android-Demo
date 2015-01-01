package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.event.OnReceiveResultEvent;
import com.jayjaylab.androiddemo.app.greyhound.fragment.FragmentPathHistory;
import com.jayjaylab.androiddemo.app.greyhound.service.ServiceRecordingPath;
import com.jayjaylab.androiddemo.app.greyhound.util.Constants;
import com.jayjaylab.androiddemo.app.greyhound.util.MyResultReceiver;
import com.jayjaylab.androiddemo.main.activity.ActivityBase;
import com.jayjaylab.androiddemo.util.AndroidHelper;

import roboguice.context.event.OnCreateEvent;
import roboguice.context.event.OnDestroyEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;
import roboguice.util.Ln;

@ContentView(R.layout.activity_greyhound_main)
public class ActivityMain extends ActivityBase {

    boolean isPaused = true;
    boolean isBound = false;
    Messenger serviceMessenger;
    Message messegeToBeSentOnceConnected;

    @Inject Handler handler;
    @Inject FragmentPathHistory fragmentPathHistory;
    @Inject ServiceRecordingPath serviceRecordingPath;
//    @Inject MyResultReceiver resultReceiver;

    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.imagebutton_recordpause) ImageButton imagebuttonRecordPause;
    @InjectView(R.id.imagebutton_stop) ImageButton imagebuttonStop;
    @InjectView(R.id.textview_status) TextView textviewStatus;

    protected void onCreateEvent(@Observes OnCreateEvent event) {
        Ln.d("onCreateEvent() : event : %s, context : %s", event, this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.INTENT_FILTER_TAG));

        loadPathHistoryFragment();
        setViews();
        checkServiceRecordingPath();
    }

    protected void onDestroyEvent(@Observes OnDestroyEvent event) {
        Ln.d("onDestroyEvent() : event : %s", event);

        try {
            unbindService(connection);
        } catch(Exception e) {
            Ln.e(e);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Deprecated
    /**
     * @hide because ResultReceiver approach doesn't work as expected. BroadcastReceiver approach
     * is chosen for IPC from service to activity.
     */
    protected void onReceiveResultEvent(@Observes OnReceiveResultEvent event) {
        Ln.d("onReceiveResultEvent() : event : %s", event);
        final int resultCode = event.getResultCode();
        final Bundle resultData = event.getResultData();

        switch(resultCode) {
            case Constants.MSG_ONFINISH_START_RECORDING:
                if(resultData == null) {
                    textviewStatus.setText(R.string.recording);
                } else {
                    // TODO displays error message
                    Ln.e("cannot open file due to not enough space");
                }
                break;
            case Constants.MSG_ONFINISH_PAUSE_RECORDING:
                textviewStatus.setText(R.string.paused);
                break;
            case Constants.MSG_ONFINISH_STOP_RECORDING:
                textviewStatus.setText(R.string.done_recording);
                imagebuttonRecordPause.setImageResource(R.drawable.record);
                unbindServiceIfNeed();
                if(resultData == null) {
                    // TODO displays an error message
                } else {
                    // stores the result in db
                    FragmentPathHistory fragmentPathHistory = (FragmentPathHistory)
                            getSupportFragmentManager().findFragmentByTag(
                                    FragmentPathHistory.TAG);
                    fragmentPathHistory.addPath(
                            (com.jayjaylab.androiddemo.app.greyhound.model.Path)
                                    resultData.getParcelable("path"));
                }
                isPaused = true;
                handleOnServiceDisconnected();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textviewStatus.setText("");
                    }
                }, 1000);
                break;
            case Constants.MSG_NO_GOOGLE_SERVICE:
                break;
            case Constants.MSG_ONFINISH_ASK_RECORDING_STATUS:
                Ln.d("onReceiveResult() : context : %s", ActivityMain.this);
                if(resultData == null) {
                    // TODO what else should be done?
                } else {
                    if(resultData.getBoolean("isRecording")) {
                        Ln.d("333");
                        isPaused = false;
                        textviewStatus.setText(R.string.recording);
                        imagebuttonRecordPause.setImageResource(R.drawable.pause);
                    } else {
                        Ln.d("444");
                        isPaused = true;
                        textviewStatus.setText(R.string.paused);
                        imagebuttonRecordPause.setImageResource(R.drawable.record);
                    }
                }
                enableButtons(true);
                break;
        }
    }

    protected void setViews() {
        setToolbar();
        setImageButtonRecordPause();
        setImageButtonStop();
    }

    protected void checkServiceRecordingPath() {
        Ln.d("checkServiceRecordingPath()");

        if(AndroidHelper.isServiceRunning(this, ServiceRecordingPath.class)) {
            Ln.d("checkServiceRecordingPath() : disabled buttons and bind to the service...");
            // because the service is now running, the service doesn't need to be started again.
            enableButtons(false);
            bindServiceIfNeeded();

            if(serviceMessenger == null) {
                messegeToBeSentOnceConnected =
                        Message.obtain(null, Constants.MSG_ASK_RECORDING_STATUS, 0, 0);
            } else {
                Message msg = Message.obtain(null, Constants.MSG_ASK_RECORDING_STATUS, 0, 0);
                try {
                    serviceMessenger.send(msg);
                } catch(RemoteException e) {
                    Ln.e(e);
                }
            }
        }
    }

    /**
     * Enables or disables buttons
     * @param enabled   true to enable buttons, false to disable buttons
     */
    protected void enableButtons(boolean enabled) {
        Ln.d("enableButtons() : enabled : %b", enabled);

        if(enabled) {
            imagebuttonRecordPause.setEnabled(true);
            imagebuttonStop.setEnabled(true);
        } else {
            imagebuttonRecordPause.setEnabled(false);
            imagebuttonStop.setEnabled(false);
        }
    }

    protected void setToolbar() {
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ln.d("onClick() : v : %s", v);
                finish();
//                NavUtils.getParentActivityIntent(ActivityMain.this);
            }
        });
    }

    protected void setImageButtonRecordPause() {
        imagebuttonRecordPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused) {
                    isPaused = false;
                    imagebuttonRecordPause.setImageResource(R.drawable.pause);
                    startRecording();
                } else {
                    isPaused = true;
                    imagebuttonRecordPause.setImageResource(R.drawable.record);
                    pauseRecording();
                }
            }
        });
    }

    protected void setImageButtonStop() {
        imagebuttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!isPaused) {
                stopRecording();
//                }
            }
        });
    }

    protected void startRecording() {
        startRecordingPathServiceIfNeeded();
        bindServiceIfNeeded();


        if(serviceMessenger == null) {
            messegeToBeSentOnceConnected =
                    Message.obtain(null, Constants.MSG_START_RECORDING, 0, 0);
        } else {
            Message msg = Message.obtain(null, Constants.MSG_START_RECORDING, 0, 0);
            try {
                serviceMessenger.send(msg);
            } catch(RemoteException e) {
                Ln.e(e);
            }
        }
    }

    protected void pauseRecording() {
        startRecordingPathServiceIfNeeded();
        bindServiceIfNeeded();

        if(serviceMessenger == null) {
            messegeToBeSentOnceConnected =
                    Message.obtain(null, Constants.MSG_PAUSE_RECORDING, 0, 0);
        } else {
            Message msg = Message.obtain(null, Constants.MSG_PAUSE_RECORDING, 0, 0);
            try {
                serviceMessenger.send(msg);
            } catch(RemoteException e) {
                Ln.e(e);
            }
        }
    }

    protected void stopRecording() {
        startRecordingPathServiceIfNeeded();
        bindServiceIfNeeded();

        if(serviceMessenger == null) {
            messegeToBeSentOnceConnected =
                    Message.obtain(null, Constants.MSG_STOP_RECORDING, 0, 0);
        } else {
            Message msg = Message.obtain(null, Constants.MSG_STOP_RECORDING, 0, 0);
            try {
                serviceMessenger.send(msg);
            } catch(RemoteException e) {
                Ln.e(e);
            }
        }
    }

    protected void startRecordingPathServiceIfNeeded() {
        Ln.d("startRecordingPathServiceIfNeeded()");

        if(!AndroidHelper.isServiceRunning(this, ServiceRecordingPath.class)) {
            Intent intent = new Intent(this, ServiceRecordingPath.class);
//            intent.putExtra("receiver", resultReceiver.getResultReceiver());
            startService(intent);
            Ln.d("startRecordingPathServiceIfNeeded() : service gets started...");
        }
    }

    protected void bindServiceIfNeeded() {
        Ln.d("bindServiceIfNeeded() : isBound : %b", isBound);

        if(!isBound) {
            Intent intent = new Intent(this, ServiceRecordingPath.class);
//            intent.putExtra("receiver", resultReceiver.getResultReceiver());
            bindService(intent, connection, 0);

//            Ln.d("bindServiceIfNeeded() : resultReceiver : %s", resultReceiver);
        }
    }

    protected void unbindServiceIfNeed() {
        if(isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    protected void loadPathHistoryFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragmentPathHistory, fragmentPathHistory.TAG);
        transaction.commit();
    }

    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Ln.d("onServiceConnected() : name : %s, service : %s", name, service);
            serviceMessenger = new Messenger(service);
            if(messegeToBeSentOnceConnected != null) {
                try {
                    serviceMessenger.send(messegeToBeSentOnceConnected);
                } catch(RemoteException e) {
                    Ln.e(e);
                }
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Ln.d("onServiceDisconnected() : name : %s", name);
//            handleOnServiceDisconnected();
        }
    };

    protected void handleOnServiceDisconnected() {
        serviceMessenger = null;
        messegeToBeSentOnceConnected = null;
        isBound = false;
    }

    protected RoboBroadcastReceiver broadcastReceiver = new RoboBroadcastReceiver() {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            final int resultCode = intent.getIntExtra("resultCode", -1);
            final Bundle resultData = intent.getBundleExtra("resultData");

            switch(resultCode) {
                case Constants.MSG_ONFINISH_START_RECORDING:
                    if(resultData == null) {
                        textviewStatus.setText(R.string.recording);
                    } else {
                        // TODO displays error message
                        Ln.e("cannot open file due to not enough space");
                    }
                    break;
                case Constants.MSG_ONFINISH_PAUSE_RECORDING:
                    textviewStatus.setText(R.string.paused);
                    break;
                case Constants.MSG_ONFINISH_STOP_RECORDING:
                    textviewStatus.setText(R.string.done_recording);
                    imagebuttonRecordPause.setImageResource(R.drawable.record);
                    unbindServiceIfNeed();
                    if(resultData == null) {
                        // TODO displays an error message
                    } else {
                        // stores the result in db
                        FragmentPathHistory fragmentPathHistory = (FragmentPathHistory)
                                getSupportFragmentManager().findFragmentByTag(
                                        FragmentPathHistory.TAG);
                        fragmentPathHistory.addPath(
                                (com.jayjaylab.androiddemo.app.greyhound.model.Path)
                                        resultData.getParcelable("path"));
                    }
                    isPaused = true;
                    handleOnServiceDisconnected();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textviewStatus.setText("");
                        }
                    }, 1000);
                    break;
                case Constants.MSG_NO_GOOGLE_SERVICE:
                    break;
                case Constants.MSG_ONFINISH_ASK_RECORDING_STATUS:
                    Ln.d("onReceiveResult() : context : %s", ActivityMain.this);
                    if(resultData == null) {
                        // TODO what else should be done?
                    } else {
                        if(resultData.getBoolean("isRecording")) {
                            Ln.d("333");
                            isPaused = false;
                            textviewStatus.setText(R.string.recording);
                            imagebuttonRecordPause.setImageResource(R.drawable.pause);
                        } else {
                            Ln.d("444");
                            isPaused = true;
                            textviewStatus.setText(R.string.paused);
                            imagebuttonRecordPause.setImageResource(R.drawable.record);
                        }
                    }
                    enableButtons(true);
                    break;
            }
        }
    };

//    protected ResultReceiver resultReceiver = new ResultReceiver(handler) {
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            Ln.d("onReceiveResult() : resultCode : %d, resultData : %s, resultReceiver : %s",
//                    resultCode, resultData, resultReceiver);
//            switch(resultCode) {
//                case Constants.MSG_ONFINISH_START_RECORDING:
//                    if(resultData == null) {
//                        textviewStatus.setText(R.string.recording);
//                    } else {
//                        // TODO displays error message
//                        Ln.e("cannot open file due to not enough space");
//                    }
//                    break;
//                case Constants.MSG_ONFINISH_PAUSE_RECORDING:
//                    textviewStatus.setText(R.string.paused);
//                    break;
//                case Constants.MSG_ONFINISH_STOP_RECORDING:
//                    textviewStatus.setText(R.string.done_recording);
//                    imagebuttonRecordPause.setImageResource(R.drawable.record);
//                    unbindServiceIfNeed();
//                    if(resultData == null) {
//                        // TODO displays an error message
//                    } else {
//                        // stores the result in db
//                        FragmentPathHistory fragmentPathHistory = (FragmentPathHistory)
//                                getSupportFragmentManager().findFragmentByTag(
//                                        FragmentPathHistory.TAG);
//                        fragmentPathHistory.addPath(
//                                (com.jayjaylab.androiddemo.app.greyhound.model.Path)
//                                        resultData.getParcelable("path"));
//                    }
//                    isPaused = true;
//                    handleOnServiceDisconnected();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            textviewStatus.setText("");
//                        }
//                    }, 1000);
//                    break;
//                case Constants.MSG_NO_GOOGLE_SERVICE:
//                    break;
//                case Constants.MSG_ONFINISH_ASK_RECORDING_STATUS:
//                    Ln.d("onReceiveResult() : context : %s", ActivityMain.this);
//                    if(resultData == null) {
//                        // TODO what else should be done?
//                    } else {
//                        if(resultData.getBoolean("isRecording")) {
//                            Ln.d("333");
//                            isPaused = false;
//                            textviewStatus.setText(R.string.recording);
//                            imagebuttonRecordPause.setImageResource(R.drawable.pause);
//                        } else {
//                            Ln.d("444");
//                            isPaused = true;
//                            textviewStatus.setText(R.string.paused);
//                            imagebuttonRecordPause.setImageResource(R.drawable.record);
//                        }
//                    }
//                    enableButtons(true);
//                    break;
//            }
//        }
//    };
}
