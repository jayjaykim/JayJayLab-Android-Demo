package com.jayjaylab.androiddemo.event;

import android.view.View;

/**
 * Created by jongjoo on 11/13/14.
 */
public class OnClickEvent {
    View view;
    int which;

    public OnClickEvent(View view, int which) {
        this.view = view;
        this.which = which;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("view : " + view);
        builder.append(", which : " + which);
        return builder.toString();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getWhich() {
        return which;
    }

    public void setWhich(int which) {
        this.which = which;
    }
}
