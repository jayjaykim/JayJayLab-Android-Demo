package com.jayjaylab.androiddemo.main;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jayjaylab.androiddemo.R;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import roboguice.activity.RoboSplashActivity;


public class ActivityIntro extends RoboSplashActivity {
    private static final int RED = 0xffFF8080;
    private static final int BLUE = 0xff8080FF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        setViews();
    }

    private void setViews() {
        setTextCompany();
    }

    private void setTextCompany() {
        TextView text = (TextView)findViewById(R.id.text_company);

        ValueAnimator colorAnim = ObjectAnimator.ofInt(text, "textColor", RED, BLUE);
        colorAnim.setDuration(1000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    @Override
    protected void startNextActivity() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
    }
}
