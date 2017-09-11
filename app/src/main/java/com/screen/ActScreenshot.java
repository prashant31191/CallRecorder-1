package com.screen;

/**
 * Created by prashant.patel on 9/11/2017.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

public class ActScreenshot extends Activity {
    private static final int REQUEST_SCREENSHOT=59706;
    private MediaProjectionManager mgr;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);

        startActivityForResult(mgr.createScreenCaptureIntent(),
                REQUEST_SCREENSHOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_SCREENSHOT) {
            if (resultCode==RESULT_OK) {
                Intent i=
                        new Intent(this, ScreenshotService.class)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);

                startService(i);
            }
        }

        finish();
    }
}