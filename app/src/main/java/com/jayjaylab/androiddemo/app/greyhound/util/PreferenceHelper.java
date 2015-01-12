package com.jayjaylab.androiddemo.app.greyhound.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jongjoo on 1/12/15.
 */
public class PreferenceHelper {
    public static final String KEY_GPX_FILE_PATH = "gpxfile_path";
    public static final String KEY_LOCATION_RECORDING_STATE = "location_recording_state";

    // Recording state
    public static final int RECORDING_STATE_IDLE = 0;
    public static final int RECORDING_STATE_RECORDING = 1;
    public static final int RECORDING_STATE_PAUSED = 2;
    public static final int RECORDING_STATE_STOPPED = 3;

    @SuppressWarnings("unused")
    public static String getGpxFilePath(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(KEY_GPX_FILE_PATH, null);
    }

    @SuppressWarnings("unused")
    public static void setGpxFilePath(Context context, String path) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_GPX_FILE_PATH, path);
        editor.commit();
    }

    @SuppressWarnings("unused")
    public static int getLocationRecordingState(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(KEY_LOCATION_RECORDING_STATE, RECORDING_STATE_IDLE);
    }

    @SuppressWarnings("unused")
    public static void setLocationRecordingState(Context context, int state) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_LOCATION_RECORDING_STATE, state);
        editor.commit();
    }
}
