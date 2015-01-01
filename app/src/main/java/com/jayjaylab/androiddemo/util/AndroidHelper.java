package com.jayjaylab.androiddemo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by jongjoo on 11/23/14.
 */
public class AndroidHelper {
    /**
     * Gets resource ID of the resource
     * @param resourceName  resource name
     * @param clazz an instance of Class, e.g. R.drawable.class
     * @return  an resource ID
     */
    public static int getId(String resourceName, Class<?> clazz) {
        try {
            Field idField = clazz.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + clazz, e);
        }
    }

    /**
     * Returns true if <var>serviceClass</var> is running for the moment, false otherwise.
     *
     * @param context   an instance of Context class
     * @param serviceClass  class instance
     * @return Returns true if <var>serviceClass</var> is running for the moment, false otherwise.
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    public static boolean makeDirectory(String dirPath) {
        File file = new File(dirPath);
        return file.mkdirs() || file.isDirectory();
    }

    public static long getFreeSpace(String path) {
        File file = new File(path);
        if(!(file.isDirectory() && file.exists())) {
            file.mkdirs();
            if(file.isDirectory()) {
                return file.getFreeSpace();
            } else {
                return 0L;
            }
        } else {
            return file.getFreeSpace();
        }
    }
}
