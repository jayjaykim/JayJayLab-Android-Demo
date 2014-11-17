package com.jayjaylab.androiddemo.app.greyhound.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayjaylab.androiddemo.R;

import roboguice.fragment.RoboListFragment;


/**
 * Created by jongjoo on 11/17/14.
 */
public class FragmentPathHistory extends RoboListFragment {
    public static final String TAG = FragmentPathHistory.class.getSimpleName();

    // views

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO RecyclerView and CardView
        // TODO loads the recent 10 records if any
        setViews();
    }

    protected void setViews() {

    }
}
