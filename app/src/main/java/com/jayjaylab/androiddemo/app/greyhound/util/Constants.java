package com.jayjaylab.androiddemo.app.greyhound.util;

/**
 * Created by jongjoo on 11/28/14.
 */
public class Constants {
    // message ids sent from activity to service
    public static final int MSG_START_RECORDING = 0;
    public static final int MSG_PAUSE_RECORDING = 1;
    public static final int MSG_STOP_RECORDING = 2;

    // message ids sent from service to activity
    public static final int MSG_ONFINISH_START_RECORDING = 101;
    public static final int MSG_ONFINISH_PAUSE_RECORDING = 102;
    public static final int MSG_ONFINISH_STOP_RECORDING = 103;
    public static final int MSG_NO_GOOGLE_SERVICE = 104;
}
