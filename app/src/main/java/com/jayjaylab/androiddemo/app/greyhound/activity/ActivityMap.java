package com.jayjaylab.androiddemo.app.greyhound.activity;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Point;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.inject.Inject;
import com.jayjaylab.androiddemo.R;
import com.jayjaylab.androiddemo.app.greyhound.event.OnGPXParsingCompleteEvent;
import com.jayjaylab.androiddemo.app.greyhound.util.GPXParser;
import com.jayjaylab.androiddemo.main.activity.ActivityBase;

import java.util.List;

import roboguice.context.event.OnCreateEvent;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

/**
 * Created by jongjoo on 1/2/15.
 */
@ContentView(R.layout.activity_greyhound_map)
public class ActivityMap extends ActivityBase implements
        GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    @Inject GPXParsingTask gpxParsingTask;
    SupportMapFragment fragmentMap;
    GoogleMap googleMap;

    // views
    @InjectView(R.id.toolbar) Toolbar toolbar;

    protected void onCreateEvent(@Observes OnCreateEvent event) {
        Ln.d("onCreateEvent() : event : %s", event);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setViews();

        gpxParsingTask.setGpxFilePath(getIntent().getStringExtra("gpxFilePath"));
        gpxParsingTask.execute();
    }

    protected void handleOnGPXParsingCompleteEvent(@Observes OnGPXParsingCompleteEvent event) {
        Ln.d("handleOnGPXParsingCompleteEvent() : event : %s", event);

        // draws Polyline on the map
        PolylineOptions polylineOptions = new PolylineOptions();
        List<Location> list = event.getLocationList();
        for(Location location : list) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            polylineOptions.add(latLng);

            if(googleMap != null) {
                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        }

        if(googleMap != null) {
            googleMap.addPolyline(polylineOptions);

            if(list.size() >= 1) {
                googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                new LatLng(list.get(list.size() - 1).getLatitude(),
                                        list.get(list.size() - 1).getLongitude()), 16.0f));

            }
        }
    }

    protected void setViews() {
        setToolbar();
        setFragmentMap();
    }

    protected void setFragmentMap() {
        Ln.d("setFragmentMap()");
        fragmentMap = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        fragmentMap.getMapAsync(this);
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

    @Override
    public void onMapLoaded() {
        Ln.d("onMapLoaded()");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Ln.d("onMapReady() : googleMap : %s", googleMap);
        this.googleMap = googleMap;
    }

    public static class GPXParsingTask extends RoboAsyncTask<List<Location>> {
        String gpxFilePath;
        @Inject EventManager eventManager;

        @Inject
        public GPXParsingTask(Context context) {
            super(context);
        }

        public void setGpxFilePath(String path) {
            Ln.d("setGpxFilePath() : path : %s", path);
            gpxFilePath = path;
        }

        @Override
        public List<Location> call() throws Exception {
            return GPXParser.parse(gpxFilePath);
        }

        @Override
        protected void onSuccess(List<Location> locations) throws Exception {
            eventManager.fire(new OnGPXParsingCompleteEvent(locations));
            super.onSuccess(locations);
        }

        @Override
        protected void onInterrupted(Exception e) {
            super.onInterrupted(e);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onThrowable(Throwable t) throws RuntimeException {
            super.onThrowable(t);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
        }
    }
}
