package com.jayjaylab.androiddemo.util;

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
}
