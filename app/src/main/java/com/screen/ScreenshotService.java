package com.screen;

/**
 * Created by prashant.patel on 9/11/2017.
 */


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.Utils.AppFlags;
import com.azapps.callrecorder.BuildConfig;
import com.azapps.callrecorder.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotService extends Service {
    private static final int NOTIFY_ID=9906;
    static final String EXTRA_RESULT_CODE="resultCode";
    static final String EXTRA_RESULT_INTENT="resultIntent";
    static final String ACTION_RECORD=
            BuildConfig.APPLICATION_ID+".RECORD";
    static final String ACTION_SHUTDOWN=
            BuildConfig.APPLICATION_ID+".SHUTDOWN";
    static final int VIRT_DISPLAY_FLAGS=
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread=
            new HandlerThread(getClass().getSimpleName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultData;
    final private ToneGenerator beeper=
            new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    @Override
    public void onCreate() {
        super.onCreate();

        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr=(WindowManager)getSystemService(WINDOW_SERVICE);

        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        AppFlags.showLog("======onStartCommand=============");
        if (i.getAction()==null) {
            AppFlags.showLog("=======111111=====");
            resultCode=i.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData=i.getParcelableExtra(EXTRA_RESULT_INTENT);
            ///---
          //  foregroundify();

            setAutoScreenCapture();
        }
        else if (ACTION_RECORD.equals(i.getAction())) {
            AppFlags.showLog("=======222222=====");
            if (resultData!=null) {
                startCapture();
            }
            else {
                Intent ui=
                        new Intent(this, ActScreenshot.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(ui);
            }
        }
        else if (ACTION_SHUTDOWN.equals(i.getAction())) {
            AppFlags.showLog("=======333333=====");
            beeper.startTone(ToneGenerator.TONE_PROP_NACK);
            stopForeground(true);
            stopSelf();
        }

        return(START_NOT_STICKY);
        //return(START_STICKY);
    }

    @Override
    public void onDestroy() {
        stopCapture();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    WindowManager getWindowManager() {
        return(wmgr);
    }

    Handler getHandler() {
        return(handler);
    }

    @SuppressLint("NewApi")
    void processImage(final byte[] png) {
        new Thread() {
            @Override
            public void run() {

                SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmmss");
                String currentDateTime = sdf.format(new Date());
                AppFlags.showLog("======File-Name======");
                 //File output=new File(getExternalFilesDir(null),"screenshot.png");
                File output=new File(getExternalFilesDir(null),currentDateTime+"_s.png");


                try {
                    FileOutputStream fos=new FileOutputStream(output);

                    AppFlags.showLog("======output====="+output.getAbsolutePath());

                    fos.write(png);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();

                    MediaScannerConnection.scanFile(ScreenshotService.this,
                            new String[] {output.getAbsolutePath()},
                            new String[] {"image/png"},
                            null);
                }
                catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception writing out screenshot", e);
                }
            }
        }.start();

        beeper.startTone(ToneGenerator.TONE_PROP_ACK);
        stopCapture();
    }

    @SuppressLint("NewApi")
    private void stopCapture() {
        AppFlags.showLog("==============stopCapture======");
        if (projection!=null) {
            projection.stop();
            vdisplay.release();
            projection=null;
        }
    }

    @SuppressLint("NewApi")
    private void startCapture() {
        AppFlags.showLog("==============startCapture======");
        projection = mgr.getMediaProjection(resultCode, resultData);
        it=new ImageTransmogrifier(this);

        MediaProjection.Callback cb=new MediaProjection.Callback() {
            @Override
            public void onStop() {
                vdisplay.release();
            }
        };

        vdisplay = projection.createVirtualDisplay("andshooter",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
        projection.registerCallback(cb, handler);
    }

    @SuppressLint("NewApi")
    private void foregroundify() {
        NotificationCompat.Builder b=
                new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        b.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(getString(R.string.app_name));

        b.addAction(R.drawable.ic_fiber_manual_record_white_24dp,
                getString(R.string.notify_record),
                buildPendingIntent(ACTION_RECORD));

        b.addAction(R.drawable.ic_play_arrow_black_36dp,
                getString(R.string.notify_shutdown),
                buildPendingIntent(ACTION_SHUTDOWN));

        startForeground(NOTIFY_ID, b.build());
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i=new Intent(this, getClass());
        i.setAction(action);
        return(PendingIntent.getService(this, 0, i, 0));
    }




    int stopCapture = 5;
    int iStop=0;

    private  void  setAutoScreenCapture()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(stopCapture > iStop)
                {
                    AppFlags.showLog("==setAutoScreenCapture=========iStop===="+iStop);

                    startCapture();


                    //Intent i = new Intent(ScreenshotService.this, getClass());
                    //i.setAction(ACTION_RECORD);
                 //   PendingIntent.getService(ScreenshotService.this, 0, i, 0);



                    iStop = iStop + 1;

                    setAutoScreenCapture();
                }
                else
                {
                    AppFlags.showLog("=====Stop & shut down=========");

                    beeper.startTone(ToneGenerator.TONE_PROP_NACK);
                    stopForeground(true);
                    stopSelf();


                }
            }
        },7000);


    }

}