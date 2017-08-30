package com.azapps.callrecorder;

/**
 * Created by prashant.patel on 8/30/2017.
 */

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.piterwilson.audio.MP3RadioStreamDelegate;
import com.piterwilson.audio.MP3RadioStreamPlayer;
import com.shuyu.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.azapps.callrecorder.MainFragment.dip2px;
import static com.azapps.callrecorder.MainFragment.getScreenWidth;


public class WavePlayActivity extends AppCompatActivity implements MP3RadioStreamDelegate {

    private final static String TAG = "WavePlayActivity";

    @BindView(R.id.audioWave)
    AudioWaveView audioWave;
    @BindView(R.id.activity_wave_play)
    RelativeLayout activityWavePlay;
    @BindView(R.id.playBtn)
    Button playBtn;
    @BindView(R.id.playOther)
    Button playOther;
    @BindView(R.id.seekBar)
    SeekBar seekBar;


    MP3RadioStreamPlayer player;

    Timer timer;

    boolean playeEnd;

    boolean seekBarTouch;
    String strUriPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_play);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, 1000);
        playBtn.setEnabled(false);
        seekBar.setEnabled(false);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarTouch = false;
                if (!playeEnd) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (playeEnd || player == null || !seekBar.isEnabled()) {
                    return;
                }
                long position = player.getCurPosition();
                if (position > 0 && !seekBarTouch) {
                    seekBar.setProgress((int) position);
                }
            }
        }, 1000, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioWave.stopView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stop();
    }

    @OnClick(R.id.playBtn)
    public void onClick() {

        if (playeEnd) {
            stop();
            playBtn.setText("Pause");
            seekBar.setEnabled(true);
            play();
            return;
        }

        if (player.isPause()) {
            playBtn.setText("Pause");
            player.setPause(false);
            seekBar.setEnabled(false);
        } else {
            playBtn.setText("Play");
            player.setPause(true);
            seekBar.setEnabled(true);
        }

    }



    private void play() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        player = new MP3RadioStreamPlayer();
        //player.setUrlString(this, true, "http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2005.mp3");

        if(strUriPath.length() < 2)
        {
            strUriPath = getIntent().getStringExtra("uri");
        }
        player.setUrlString(strUriPath);
        player.setDelegate(this);

        int size = getScreenWidth(this) / dip2px(this, 1);//控件默认的间隔是1
        player.setDataList(audioWave.getRecList(), size);

        //player.setStartWaveTime(5000);
        //audioWave.setDrawBase(false);
        audioWave.setBaseRecorder(player);
        audioWave.startView();
        try {
            player.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stop() {
        player.stop();
    }


    /****************************************
     * Delegate methods. These are all fired from a background thread so we have to call any GUI code on the main thread.
     ****************************************/

    @Override
    public void onRadioPlayerPlaybackStarted(final MP3RadioStreamPlayer player) {
        Log.i(TAG, "onRadioPlayerPlaybackStarted");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                playeEnd = false;
                playBtn.setEnabled(true);
                seekBar.setMax((int) player.getDuration());
                seekBar.setEnabled(true);
            }
        });
    }

    @Override
    public void onRadioPlayerStopped(MP3RadioStreamPlayer player) {
        Log.i(TAG, "onRadioPlayerStopped");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                playeEnd = true;
                playBtn.setText("Play");
                playBtn.setEnabled(true);
                seekBar.setEnabled(false);
            }
        });

    }

    @Override
    public void onRadioPlayerError(MP3RadioStreamPlayer player) {
        Log.i(TAG, "onRadioPlayerError");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                playeEnd = false;
                playBtn.setEnabled(true);
                seekBar.setEnabled(false);
            }
        });

    }

    @Override
    public void onRadioPlayerBuffering(MP3RadioStreamPlayer player) {
        Log.i(TAG, "onRadioPlayerBuffering");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                playBtn.setEnabled(false);
                seekBar.setEnabled(false);
            }
        });

    }

    int REQ_CODE_PICK_SOUNDFILE = 303;
    @OnClick(R.id.playOther)
    public void onClickChoose()
    {

        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Choose audio file"), REQ_CODE_PICK_SOUNDFILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                Uri audioFileUri = data.getData();
                Log.d("media", "onActivityResult: "+audioFileUri);
                // Now you can use that Uri to get the file path, or upload it, ...

                strUriPath = getPath(WavePlayActivity.this,audioFileUri);
                Log.d("media", "strUriPath: "+strUriPath);
                play();
            }
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    String docId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        docId = DocumentsContract.getDocumentId(uri);
                    }
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    String id = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        id = DocumentsContract.getDocumentId(uri);
                    }
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}