package com.aykuttasil.callrecorder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.Utils.CallRecord;
import com.jlcsoftware.callrecorder.R;


public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    CallRecord callRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //callRecord = CallRecord.init(this);

        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("CallRecorderFile")
                .setRecordDirName("CallRecorderDir")
                .setShowSeed(true)
                .build();

        //callRecord.changeReceiver(new MyCallRecordReceiver(callRecord));

        //callRecord.enableSaveFile();


        /*
        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("Record_" + new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US).format(new Date()))
                .setRecordDirName("CallRecord")
                .setRecordDirPath(Environment.getExternalStorageDirectory().getPath())
                .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setShowSeed(true)
                .buildService();

        callRecord.startCallRecordService();
        */

    }

    public void StartCallRecordClick(View view) {
        Log.i("CallRecord", "StartCallRecordClick");
        callRecord.startCallReceiver();

        //callRecord.enableSaveFile();
        //callRecord.changeRecordDirName("NewDirName");
    }

    public void StopCallRecordClick(View view) {
        Log.i("CallRecord", "StopCallRecordClick");
        callRecord.stopCallReceiver();

        //callRecord.disableSaveFile();
        //callRecord.changeRecordFileName("NewFileName");
    }
}
