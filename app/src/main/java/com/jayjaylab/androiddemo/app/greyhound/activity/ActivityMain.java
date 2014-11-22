package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.fragment.FragmentPathHistory;

import roboguice.activity.RoboActionBarActivity;
import roboguice.context.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_greyhound_main)
public class ActivityMain extends RoboActionBarActivity {

    @Inject FragmentPathHistory fragmentPathHistory;
    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.imageview_recordpause) TintImageView imageviewRecordPause;
    @InjectView(R.id.imageview_stop) TintImageView imageviewStop;

    public void onCreateEvent(@Observes OnCreateEvent event) {
        setSupportActionBar(toolbar);
        loadPathHistoryFragment();

        setViews();
    }

    protected void setViews() {

    }

    protected void loadPathHistoryFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragmentPathHistory, fragmentPathHistory.TAG);
        transaction.commit();
    }
}
