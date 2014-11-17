package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jayjaylab.androiddemo.R;

import roboguice.activity.RoboActionBarActivity;
import roboguice.context.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_greyhound_main)
public class ActivityMain extends RoboActionBarActivity {

    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;

    public void onCreateEvent(@Observes OnCreateEvent event) {
        setSupportActionBar(toolbar);
    }
}
