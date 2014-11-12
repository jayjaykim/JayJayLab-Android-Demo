package com.jayjaylab.androiddemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import roboguice.util.Ln;

/**
 * Created by jongjoo on 11/12/14.
 */
public class CardView extends android.support.v7.widget.CardView {
    public CardView(Context context) {
        super(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Ln.d("dispatchTouchEvent() : event : %s", event);
        final int action = event.getAction();
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {

        } else if(action == MotionEvent.ACTION_DOWN) {

        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Ln.d("onInterceptTouchEvent() : event : %s", event);
        final int action = event.getAction();
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {

        } else if(action == MotionEvent.ACTION_DOWN) {

        }

        return super.onInterceptTouchEvent(event);
    }
}
