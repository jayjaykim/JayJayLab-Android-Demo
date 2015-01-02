package com.jayjaylab.androiddemo.main.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jayjaylab.androiddemo.dialog.ProgressDialog;
import com.jayjaylab.androiddemo.event.ProgressBarEvent;

import roboguice.activity.RoboActionBarActivity;
import roboguice.event.Observes;
import roboguice.util.Ln;

/**
 * Created by jongjoo on 12/30/14.
 */
public class ActivityBase extends RoboActionBarActivity {

    @SuppressWarnings("unused")
    protected void handleProgressBarEvent(@Observes ProgressBarEvent event) {
        Ln.d("handleProgressBarEvent() : event : %s", event);

        if(event.getVisibility()) {
            if(event.getStringResourceId() == 0) {
                Ln.d("handleProgressBarEvent() with no text");
                showProgressDialog();
            } else {
                Ln.d("handleProgressBarEvent() with text");
                showProgressDialog(event.getStringResourceId());
            }
        } else {
            dismissProgressDialog();
        }
    }

    protected void showProgressDialog() {
        ProgressDialog dialog = new ProgressDialog();
        dialog.show(getSupportFragmentManager(), ProgressDialog.TAG);
    }

    protected void showProgressDialog(int stringResId) {
        ProgressDialog dialog = new ProgressDialog();
        Bundle args = new Bundle();
        args.putInt("stringResId", stringResId);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), ProgressDialog.TAG);
    }

    protected void dismissProgressDialog() {
        final DialogFragment dialog = (DialogFragment)
                getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);

        Ln.d("dismissProgressDialog() : dialog : %s", dialog);
        if(dialog != null) {
            try {
                dialog.dismiss();
            } catch(Exception e) {
                Ln.e(e);
            }
        }
    }
}
