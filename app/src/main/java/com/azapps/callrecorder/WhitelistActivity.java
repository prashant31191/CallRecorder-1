package com.azapps.callrecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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
        setContentView(R.layout.activity_whitelist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_whitelist, menu);
        fragment = (WhitelistFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
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
        MenuItem menuItem = optionsMenu.findItem(R.id.action_delete);
        menuItem.setVisible(item.length>0);
    }

}
