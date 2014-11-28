package com.jayjaylab.androiddemo.util;

import android.app.ActivityManager;
import android.content.Context;

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

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
