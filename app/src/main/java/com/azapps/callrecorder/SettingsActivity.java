package com.azapps.callrecorder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.Utils.App;
import com.Utils.FlagData;
import com.azapps.database.CallLog;
import com.azapps.database.Database;
import com.azapps.receivers.MyAlarmReceiver;
import com.azapps.services.CleanupService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.polaric.colorful.ColorPickerDialog;
import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Not your classic Android Settings Activity, since
 * we are only supporting a very limited range of options and PreferenceActivity is essentially deprecated
 * and all the Fragment stuff is over-kill
 * <p>
 * a new PreferencesFragment based Activity is overkill and the old PreferenceActivity never made much sense anyway
 */

public class SettingsActivity extends ColorfulActivity //AppCompatActivity {
{

    String TAG = "SettingsActivity";


    RelativeLayout rlPrimary, rlAccent;
    ImageView ivPrimary, ivAccent;
    SwitchCompat swIsLight;

    private RewardedVideoAd mRewardedVideoAd;
    Button button_video;
    Button button_theme;
    boolean isClickEnable = false;
    boolean isDark = false;


    class MyArrayAdapter<T> extends ArrayAdapter<T> {

        ArrayList<Integer> icons;

        public MyArrayAdapter(Context context, List objects, ArrayList<Integer> icons) {
            super(context, android.R.layout.simple_spinner_item, objects);
            this.icons = icons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(icons.get(position), 0, 0, 0);
            return view;
        }
    }

    @Override
    public void onStop() {
        final AppPreferences.OlderThan olderThan = AppPreferences.getInstance(this).getOlderThan();
        if (olderThan != AppPreferences.OlderThan.NEVER) {
            MyAlarmReceiver.setAlarm(SettingsActivity.this);
        } else {
            MyAlarmReceiver.cancleAlarm(SettingsActivity.this);
        }
        super.onStop();
    }

    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_settings);


            preferences = AppPreferences.getInstance(this);

            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
            checkBox.setChecked(preferences.isRecordingIncomingEnabled());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    preferences.setRecordingIncomingEnabled(isChecked);
                }
            });
            checkBox = (CheckBox) findViewById(R.id.checkBox2);
            checkBox.setChecked(preferences.isRecordingOutgoingEnabled());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    preferences.setRecordingOutgoingEnabled(isChecked);
                }
            });

            // File[] externalFilesDirs = new ContextCompat().getExternalFilesDirs(this, null);
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            List<String> list = new ArrayList<String>();
            ArrayList<Integer> icons = new ArrayList<>();

            File filesDir = getFilesDir();
            list.add(filesDir.getAbsolutePath());
            icons.add(R.drawable.ic_folder_black_24dp);

      /*  for (File file : externalFilesDirs) {
            list.add(file.getAbsolutePath());
            icons.add(R.drawable.ic_cards_black_24);
        }*/
            final MyArrayAdapter<String> dataAdapter = new MyArrayAdapter<String>(this, list, icons);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String path = dataAdapter.getItem(position);
                    calcFreeSpace(path);
                    AppPreferences.getInstance(getApplicationContext()).setFilesDirectory(path);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            String path = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory().getAbsolutePath();
            spinner.setSelection(dataAdapter.getPosition(path.replace("/calls/", "")));
            calcFreeSpace(path);


            // Now, count the recordings
            ArrayList<CallLog> allCalls = Database.getInstance(getApplicationContext()).getAllCalls();
            TextView textView = (TextView) findViewById(R.id.textView4);
            String str = textView.getText().toString();
            str = String.format(str, allCalls.size());
            textView.setText(Html.fromHtml(str));

            // Get the length of each file...
            long length = 0;
            for (CallLog call : allCalls) {
                File file = new File(call.getPathToRecording());
                length += file.length();
            }
            textView = (TextView) findViewById(R.id.textView5);
            str = textView.getText().toString();
            str = String.format(str, length / 1024);
            textView.setText(Html.fromHtml(str));

            spinner = (Spinner) findViewById(R.id.spinner2);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Obviously <string-array name="pref_frequencies"> MUST be in the same order as AppPreferences.OlderThan enum
                    final AppPreferences.OlderThan olderThan = AppPreferences.OlderThan.values()[position];
                    AppPreferences.getInstance(getApplicationContext()).setOlderThan(olderThan);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinner.setSelection(AppPreferences.getInstance(getApplicationContext()).getOlderThan().ordinal());

            Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CleanupService.sartCleaning(SettingsActivity.this);
                }
            });

            button_video = (Button) findViewById(R.id.button_video);
            button_theme = (Button) findViewById(R.id.button_theme);
            isDark = App.isDarkTheme();

            button_theme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDark == false) {
                        isDark = true;
                    } else {
                        isDark = false;
                    }

                    App.setApplyThemeRuntimeDark(isDark);
                }
            });


            rlPrimary = (RelativeLayout) findViewById(R.id.rlPrimary);
            rlAccent = (RelativeLayout) findViewById(R.id.rlAccent);

            swIsLight = (SwitchCompat) findViewById(R.id.swIsLight);

            ivPrimary = (ImageView) findViewById(R.id.ivPrimary);
            ivAccent = (ImageView) findViewById(R.id.ivAccent);

            ivPrimary.setBackgroundResource(Colorful.getThemeDelegate().getPrimaryColor().getColorRes());
            ivAccent.setBackgroundResource(Colorful.getThemeDelegate().getAccentColor().getColorRes());


            if (isDark == true) {
                swIsLight.setChecked(true);
            } else {
                swIsLight.setChecked(false);
            }

            swIsLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isDark = isChecked;

                    App.setApplyThemeRuntimeDark(isDark);
                }
            });


            rlPrimary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showColorPicker(true);
                }
            });

            rlAccent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showColorPicker(false);
                }
            });


            button_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showRewardedVideo();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isClickEnable == false) {
                        button_video.performClick();
                    }
                }
            }, 12000);


            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(this, FlagData.APP_ID);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    Log.i(TAG, "====onRewardedVideoAdLoaded====");
                    if (isClickEnable == false) {
                        isClickEnable = true;
                        showRewardedVideo();
                    }

                    loadRewardedVideoAd();


                }

                @Override
                public void onRewardedVideoAdOpened() {
                    Log.i(TAG, "====onRewardedVideoAdOpened====");
                }

                @Override
                public void onRewardedVideoStarted() {
                    Log.i(TAG, "====onRewardedVideoStarted====");
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    Log.i(TAG, "====onRewardedVideoAdClosed====");
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                    Log.i(TAG, "====onRewarded====");
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                    Log.i(TAG, "====onRewardedVideoAdLeftApplication====");
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    Log.i(TAG, "====onRewardedVideoAdFailedToLoad====");
                }
            });
            loadRewardedVideoAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(FlagData.AD_REV_VID_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    private void showColorPicker(final boolean isPrimary) {
        Log.i(TAG, "====showColorPicker====");

        try {
            ColorPickerDialog dialog = new ColorPickerDialog(this);
            dialog.setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
                @Override
                public void onColorSelected(Colorful.ThemeColor color) {
                    //TODO: Do something with the color
                    if (isPrimary == true) {
                        App.setApplyThemeRuntimeP(color);
                        ivPrimary.setBackgroundResource(color.getColorRes());
                    } else {
                        App.setApplyThemeRuntimeA(color);
                        ivAccent.setBackgroundResource(color.getColorRes());
                    }


                    App.showLog("====color====00==" + color.getColorRes());


                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRewardedVideo() {
        Log.i(TAG, "====showRewardedVideo====");

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRewardedVideoAd.pause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRewardedVideoAd.resume(this);
    }

    private void calcFreeSpace(String path) {
        // http://stackoverflow.com/questions/3394765/how-to-check-available-space-on-android-device-on-mini-sd-card
        StatFs stat = new StatFs(path);
        long bytesTotal = 0;
        long bytesAvailable = 0;
        float megAvailable = 0;
        long megTotalAvailable = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesTotal = (long) stat.getBlockSizeLong() * (long) stat.getBlockCountLong();
            bytesAvailable = (long) stat.getBlockSizeLong() * (long) stat.getAvailableBlocksLong();
        } else {
            bytesTotal = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
        megAvailable = bytesAvailable / 1048576;
        megTotalAvailable = bytesTotal / 1048576;

        // Free Space
        TextView textView = (TextView) findViewById(R.id.textView6);
        String str = getString(R.string.pref_folder_total_folder_size);
        str = String.format(str, megAvailable);
        textView.setText(Html.fromHtml(str));
    }

}
