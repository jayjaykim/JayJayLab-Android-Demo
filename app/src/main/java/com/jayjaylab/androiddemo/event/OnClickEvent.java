package com.jayjaylab.androiddemo.event;

import android.view.View;

/**
 * Created by jongjoo on 11/13/14.
 */
public class OnClickEvent {
    View view;
    int which;
    String tag;

    public OnClickEvent(View view, int which) {
        this.view = view;
        this.which = which;
    }

    public OnClickEvent(View view, int which, String tag) {
        this.view = view;
        this.which = which;
        this.tag = tag;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("view : " + view);
        builder.append(", which : " + which);
        builder.append(", tag : " + tag);
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
