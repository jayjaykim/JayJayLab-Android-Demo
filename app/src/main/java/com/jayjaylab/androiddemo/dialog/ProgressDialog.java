package com.jayjaylab.androiddemo.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jayjaylab.androiddemo.R;

/**
 * Defines progress dialog
 * 
 * @author jayjay
 */
public class ProgressDialog extends DialogFragment {
	public static final String TAG = ProgressDialog.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getDialog().setCancelable(false);
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setOnKeyListener(new OnKeyListener() {			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK) 
					return true;
				
				return false;
			}
		});

		View view = inflater.inflate(R.layout.progress_bar, container);

        Bundle bundle = getArguments();
        if(bundle != null) {
            int stringResId = bundle.getInt("stringResId", -1);
            if (stringResId != -1) {
                TextView message = (TextView) view.findViewById(R.id.textview);
                message.setText(stringResId);
                message.setVisibility(View.VISIBLE);
            }
        }
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		try {
			super.onActivityCreated(savedState);
		} catch(Exception e) {
			Log.e(TAG, "onActivityCreated() : exception : " + e.getMessage());
		}
	}
}
