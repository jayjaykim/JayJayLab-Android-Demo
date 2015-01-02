package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.main.activity.ActivityBase;

import roboguice.context.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by jongjoo on 1/2/15.
 */
@ContentView(R.layout.activity_greyhound_map)
public class ActivityMap extends ActivityBase {

    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;

    protected void onCreateEvent(@Observes OnCreateEvent event) {
        Ln.d("onCreateEvent() : event : %s", event);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setViews();
    }

    protected void setViews() {
        setToolbar();
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
}
