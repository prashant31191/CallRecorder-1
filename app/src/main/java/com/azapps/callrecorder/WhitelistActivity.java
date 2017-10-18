package com.azapps.callrecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.Utils.FlagData;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.polaric.colorful.ColorfulActivity;

/**
 * Manage our Whitelisted Contacts (don't record them)
 */

public class WhitelistActivity extends ColorfulActivity implements WhitelistFragment.OnListFragmentInteractionListener {

    Menu optionsMenu;
    WhitelistFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_whitelist);
            fragment = (WhitelistFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


            ImageView ivAdd = (ImageView) findViewById(R.id.ivAdd);
            ImageView ivRemove = (ImageView) findViewById(R.id.ivRemove);
            layout = (RelativeLayout) findViewById(R.id.rlAds);

            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment != null) {
                        fragment.addContact();
                    }
                }
            });
            ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment != null) {
                        fragment.removeSelectedContacts();
                    }
                }
            });

            setDisplayBanner();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    AdView mAdView;
    RelativeLayout layout;
    private void setDisplayBanner()
    {


        //String deviceid = tm.getDeviceId();

        mAdView = new AdView(WhitelistActivity.this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(FlagData.strBnrId);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        layout.addView(mAdView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("33BE2250B43518CCDA7DE426D04EE232")
                .build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice(deviceid).build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_whitelist, menu);

        optionsMenu = menu;

        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            fragment.addContact();
            return true;
        }

        if (id == R.id.action_delete) {
            fragment.removeSelectedContacts();
            return true;
        }

        return false;
    }

    @Override
    public void onListFragmentInteraction(WhitelistRecord[] item) {
        try {
            if (optionsMenu != null) {
                MenuItem menuItem = optionsMenu.findItem(R.id.action_delete);
                menuItem.setVisible(item.length > 0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
