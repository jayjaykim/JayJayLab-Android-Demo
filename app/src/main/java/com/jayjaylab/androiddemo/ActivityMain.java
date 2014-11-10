package com.jayjaylab.androiddemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.view.ImageViewThreadPool;

import roboguice.activity.RoboListActivity;
import roboguice.activity.event.OnPauseEvent;
import roboguice.context.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_main)
public class ActivityMain extends RoboListActivity {
    @Inject ImageViewThreadPool  imageViewThreadPool;

    public void onCreateEvent(@Observes OnCreateEvent event) {

    }

    public void onPauseEvent(@Observes OnPauseEvent event) {

    }
}
