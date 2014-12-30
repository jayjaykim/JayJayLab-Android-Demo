package com.jayjaylab.androiddemo.event;

/**
 * Created by jongjoo on 12/30/14.
 */
public class ProgressBarEvent {
    boolean visible;
    int stringResourceId;

    public ProgressBarEvent(boolean visible) {
        this.visible = visible;
    }

    public ProgressBarEvent(boolean visible, int stringResourceId) {
        this.visible = visible;
        this.stringResourceId = stringResourceId;
    }

    public boolean getVisibility() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getStringResourceId() {
        return stringResourceId;
    }

    public void setStringResourceId(int stringResourceId) {
        this.stringResourceId = stringResourceId;
    }

    public String toString() {
        return "visible : " + visible;
    }
}
