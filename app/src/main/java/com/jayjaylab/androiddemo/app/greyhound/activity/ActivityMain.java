package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.fragment.FragmentPathHistory;

import roboguice.activity.RoboActionBarActivity;
import roboguice.context.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_greyhound_main)
public class ActivityMain extends RoboActionBarActivity {

    boolean isPaused = false;

    @Inject FragmentPathHistory fragmentPathHistory;
    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.imagebutton_recordpause) ImageButton imagebuttonRecordPause;
    @InjectView(R.id.imagebutton_stop) ImageButton imagebuttonStop;

    public void onCreateEvent(@Observes OnCreateEvent event) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadPathHistoryFragment();
        setViews();
        // TODO gps is on ??? or turn it on
    }

    protected void setViews() {
        setToolbar();
        setImageButtonRecordPause();
        setImageButtonStop();
    }

    protected void setToolbar() {
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ln.d("onClick() : v : %s", v);
                finish();
//                NavUtils.getParentActivityIntent(ActivityMain.this);
            }
        });
    }

    protected void setImageButtonRecordPause() {
        imagebuttonRecordPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused) {
                    isPaused = false;
                    imagebuttonRecordPause.setImageResource(R.drawable.record);
                    // TODO starts recording
                } else {
                    isPaused = true;
                    imagebuttonRecordPause.setImageResource(R.drawable.pause);
                    // TODO pauses recording
                }
            }
        });
    }

    protected void setImageButtonStop() {
        imagebuttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPaused) {
                    // TODO stops recording
                }
            }
        });
    }

    protected void loadPathHistoryFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragmentPathHistory, fragmentPathHistory.TAG);
        transaction.commit();
    }
}
