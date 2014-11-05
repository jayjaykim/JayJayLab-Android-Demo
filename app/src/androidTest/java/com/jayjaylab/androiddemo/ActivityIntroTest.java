package com.jayjaylab.androiddemo;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.lang.Exception;

import static junit.framework.Assert.assertTrue;

@Config(manifest = "./app/src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ActivityIntroTest {
    @Test
    public void testIfActivityIsNotNull() throws Exception {
        Activity activity = Robolectric.buildActivity(ActivityIntro.class).create().get();
        assertTrue(activity != null);
    }
}