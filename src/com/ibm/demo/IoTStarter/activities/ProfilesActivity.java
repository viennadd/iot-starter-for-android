/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Mike Robertson - initial contribution
 *******************************************************************************/
package com.ibm.demo.IoTStarter.activities;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.ibm.demo.IoTStarter.IoTStarterApplication;
import com.ibm.demo.IoTStarter.R;
import com.ibm.demo.IoTStarter.utils.Constants;
import com.ibm.demo.IoTStarter.utils.IoTProfile;

import java.util.ArrayList;

/**
 * The Profiles activity lists saved connection profiles to use to connect to IoT.
 */
public class ProfilesActivity extends ListActivity {
    private final static String TAG = ProfilesActivity.class.getName();
    private Context context;
    private IoTStarterApplication app;
    private BroadcastReceiver profilesBroadcastReceiver;

    ListView listView;

    /**************************************************************************
     * Activity functions for establishing the activity
     **************************************************************************/

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ".onCreate() entered");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profiles);
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        context = getApplicationContext();

        listView = (ListView)findViewById(android.R.id.list);

        app = (IoTStarterApplication) getApplication();
        app.setCurrentRunningActivity(TAG);

        ArrayList<IoTProfile> profiles = (ArrayList) app.getProfiles();
        ArrayList<String> profileNames = new ArrayList<String>();
        int index;
        for (index = 0; index < profiles.size(); index++) {
            profileNames.add(profiles.get(index).getProfileName());
        }

        ArrayAdapter<String> mProfilesAdapter = new ArrayAdapter<String>(this.context, R.layout.list_item, profileNames);
        listView.setAdapter(mProfilesAdapter);

        if (profilesBroadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering LogBroadcastReceiver");
            profilesBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for logBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getApplicationContext().registerReceiver(profilesBroadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_LOG));

        // initialise
        initializeProfilesActivity();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        getApplicationContext().unregisterReceiver(profilesBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeProfilesActivity() {
        Log.d(TAG, ".initializeProfilesActivity() entered");

        Button button = (Button) findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave();
            }
        });

        button = (Button) findViewById(R.id.backButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String profileName = (String) listView.getAdapter().getItem(position);
                handleSelection(profileName);
            }
        });
    }

    private void handleSelection(String profileName) {
        Log.d(TAG, ".handleSelection() entered");

        ArrayList<IoTProfile> profiles = (ArrayList<IoTProfile>) app.getProfiles();
        for (IoTProfile profile : profiles) {
            if (profile.getProfileName().equals(profileName)) {
                app.setOrganization(profile.getOrganization());
                app.setDeviceId(profile.getDeviceID());
                app.setAuthToken(profile.getAuthorizationToken());
                break;
            }
        }
        finish();
    }

    private void handleSave() {
        Log.d(TAG, ".handleSave() entered");
        IoTProfile profile = new IoTProfile("default", app.getOrganization(), app.getDeviceId(), app.getAuthToken());
        app.getProfiles().add(profile);
        ArrayAdapter<String> adapter = (ArrayAdapter) listView.getAdapter();
        synchronized (listView.getAdapter()) {
            adapter.notify();
        }
    }

    private void handleBack() {
        Log.d(TAG, ".handleBack() entered");
        finish();
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");
        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.TEXT_EVENT)) {
            listView.getAdapter().notify();
        }
    }

    /**************************************************************************
     * Functions to handle the iot_menu bar
     **************************************************************************/

    /**
     * Switch to the Login activity.
     */
    private void openLogin() {
        Log.d(TAG, ".openLogin() entered");
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * Switch to the IoT activity.
     */
    private void openIoT() {
        Log.d(TAG, ".openIoT() entered");
        Intent iotIntent = new Intent(getApplicationContext(), IoTActivity.class);
        startActivity(iotIntent);
    }

    /**
     * Switch to the Log activity.
     */
    private void openLog() {
        Log.d(TAG, ".openLog() entered");
        Intent logIntent = new Intent(getApplicationContext(), LogExpActivity.class);
        startActivity(logIntent);
    }

    /**
     * Infalte the options iot_menu.
     * @param menu The iot_menu to create.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, ".onCreateOptions() entered");
        getMenuInflater().inflate(R.menu.profiles_menu, menu);
        return true;
    }

    /**
     * Process the selected iot_menu item.
     * @param item The selected iot_menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            /*case R.id.clear:
                app.getMessageLog().clear();
                listView.getAdapter().notify();
                return true;*/
            case R.id.action_login:
                openLogin();
                return true;
            case R.id.action_iot:
                openIoT();
                return true;
            case R.id.action_log:
                openLog();
                return true;
            /*case R.id.action_accel:
                app.toggleAccel();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
