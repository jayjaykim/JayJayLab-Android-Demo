package com.jayjaylab.androiddemo;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ActivityIntroTest extends ApplicationTestCase<Application> {
    public ActivityIntroTest() {
        super(Application.class);
    }

    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}