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
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import com.ibm.demo.IoTStarter.IoTStarterApplication;
import com.ibm.demo.IoTStarter.R;
import com.ibm.demo.IoTStarter.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Log activity displays text command messages that have been received by the application.
 * NOTE: This class is an alternative to LogExpActivity and is not currently being used.
 */
public class LogActivity extends ListActivity {
    private final static String TAG = LogActivity.class.getName();
    private Context context;
    private IoTStarterApplication app;
    private BroadcastReceiver logBroadcastReceiver;

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

        setContentView(R.layout.log);
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

        ArrayAdapter<String> mLogAdapter = new ArrayAdapter<String>(this.context, R.layout.list_item, app.getMessageLog());
        listView.setAdapter(mLogAdapter);

        if (logBroadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering LogBroadcastReceiver");
            logBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for logBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getApplicationContext().registerReceiver(logBroadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_LOG));

        // initialise
        initializeLogActivity();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        getApplicationContext().unregisterReceiver(logBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeLogActivity() {
        Log.d(TAG, ".initializeLogActivity() entered");
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
     * Functions to handle the menu bar
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
     * Infalte the options menu.
     * @param menu The menu to create.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, ".onCreateOptions() entered");
        getMenuInflater().inflate(R.menu.log_menu, menu);
        return true;
    }

    /**
     * Process the selected menu item.
     * @param item The selected menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.clear:
                app.getMessageLog().clear();
                listView.getAdapter().notify();
                return true;
            case R.id.action_login:
                openLogin();
                return true;
            case R.id.action_iot:
                openIoT();
                return true;
            case R.id.action_log:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
