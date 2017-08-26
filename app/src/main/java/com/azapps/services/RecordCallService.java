package com.azapps.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.azapps.callrecorder.AppPreferences;
import com.azapps.callrecorder.LocalBroadcastActions;
import com.azapps.callrecorder.MainActivity;
import com.azapps.callrecorder.R;
import com.azapps.database.CallLog;

import java.io.File;
import java.util.Calendar;

import google.ads.AdsDisplayUtil;

/**
 * The nitty gritty Service that handles actually recording the conversations
 */

public class RecordCallService extends Service {

    public final static String ACTION_START_RECORDING = "com.azapps.ACTION_CLEAN_UP";
    public final static String ACTION_STOP_RECORDING = "com.azapps.ACTION_STOP_RECORDING";
    public final static String EXTRA_PHONE_CALL = "com.azapps.EXTRA_PHONE_CALL";

    public RecordCallService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentValues parcelableExtra = intent.getParcelableExtra(EXTRA_PHONE_CALL);
        startRecording(new CallLog(parcelableExtra));
        return START_NOT_STICKY ;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    private CallLog phoneCall;

    boolean isRecording = false;
    //CallRecord callRecord;

    private void stopRecording() {

        if (isRecording) {
            try {
//                callRecord.stopCallReceiver();

                phoneCall.setEndTime(Calendar.getInstance());
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                phoneCall.save(getBaseContext());
                displayNotification(phoneCall);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LocalBroadcastActions.NEW_RECORDING_BROADCAST));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        phoneCall = null;
    }


    MediaRecorder mediaRecorder;


    private void startRecording(CallLog phoneCall) {
        if (!isRecording) {
            isRecording = true;
            this.phoneCall = phoneCall;
            File file = null;
            try {
                this.phoneCall.setSartTime(Calendar.getInstance());
                File dir = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory();
                mediaRecorder = new MediaRecorder();
                file = File.createTempFile("record", ".3gp", dir);
                this.phoneCall.setPathToRecording(file.getAbsolutePath());
                /*
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncodingBitRate(12200);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
*/

                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(phoneCall.getPathToRecording());

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setAudioEncodingBitRate(160 * 1024);
                }
                else {
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                }

                mediaRecorder.setAudioChannels(2);*/


                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
                isRecording = false;
                if (file != null) file.delete();
                this.phoneCall = null;
                isRecording = false;

              /*  callRecord = new CallRecord.Builder(this)
                        .setRecordFileName("CallRecorderFile")
                        .setRecordDirName("CallRecorderDir")
                        .setShowSeed(true)
                        .build();
                isRecording = true;

                callRecord.startCallReceiver();*/
            }
        }
    }

    public void displayNotification(CallLog phoneCall) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_recording_conversation_white_24);
        builder.setContentTitle(getApplicationContext().getString(R.string.notification_title));
        builder.setContentText(getApplicationContext().getString(R.string.notification_text));
        builder.setContentInfo(getApplicationContext().getString(R.string.notification_more_text));
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis())); // fake action to force PendingIntent.FLAG_UPDATE_CURRENT
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("RecordingId", phoneCall.getId());

        builder.setContentIntent(PendingIntent.getActivity(this, 0xFeed, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(0xfeed, builder.build());
    }


    public static void sartRecording(Context context, CallLog phoneCall) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_START_RECORDING);
        intent.putExtra(EXTRA_PHONE_CALL, phoneCall.getContent());
        context.startService(intent);
    }


    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_STOP_RECORDING);
        context.stopService(intent);

        AdsDisplayUtil.openBnrIntAdsScreen(context,"","");
    }


}
