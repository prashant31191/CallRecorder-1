package com.azapps.callrecorder;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by prashant.patel on 8/30/2017.
 */

import android.os.Bundle;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.FrameLayout;

        import butterknife.BindView;
        import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/11/15.
 * 声音波形，录制与播放
 */
public class ActRecording extends AppCompatActivity {


    @BindView(R.id.main_frameLayout)
    FrameLayout mainFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recording);
        ButterKnife.bind(this);

        MainFragment newFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frameLayout, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

}