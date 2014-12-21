package com.jayjaylab.androiddemo;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by jongjoo on 11/13/14.
 */
@ReportsCrashes(
        formKey = "",
        mailTo = "jayjaylab.ceo@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION,
                ReportField.APP_VERSION_NAME, ReportField.PHONE_MODEL, ReportField.STACK_TRACE,
                ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }
}
