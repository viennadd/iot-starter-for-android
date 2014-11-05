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
 * Contributors: Allan Marube, Mike Robertson
 *******************************************************************************/
package com.ibm.demo.IoTStarter.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ExpandableListView;
import com.ibm.demo.IoTStarter.IoTStarterApplication;
import com.ibm.demo.IoTStarter.R;
import com.ibm.demo.IoTStarter.utils.Constants;
import com.ibm.demo.IoTStarter.utils.PayloadAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * The Log activity displays text command messages that have been received by the application.
 */
public class LogExpActivity extends Activity {
    private final static String TAG = LogExpActivity.class.getName();
    private Context context;
    private IoTStarterApplication app;
    private BroadcastReceiver logBroadcastReceiver;

    ExpandableListView expListView; // list View
    PayloadAdapter payloadAdapter;  // ArrayAdapter for payload
    List<String> headers;  // Stores topics for each
    HashMap<String, List<String>> listChildren; // holds payload information
    List<String> tempHeaders; // temporary store of headers for JSONObject keys
    HashMap<String, List<String>> tempListChildren; // temporary store for JSONObject values.

    private boolean isJsonView = false;  //is a JsonObject payload?

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        app = (IoTStarterApplication) getApplication();
        app.setCurrentRunningActivity(TAG);

        setContentView(R.layout.logexp);
        expListView =(ExpandableListView)findViewById(android.R.id.list);

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

        headers = app.getTopicsReceived();
        listChildren = app.getPayload();
        payloadAdapter  = new PayloadAdapter(this, headers, listChildren);
        expListView.setAdapter(payloadAdapter); //set adapter

        // expListView on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                List<String> tempHeadersOnClick;
                HashMap<String, List<String>> tempListChildrenOnClick;
                if (isJsonView) {
                    tempHeadersOnClick = tempHeaders;
                    tempListChildrenOnClick = tempListChildren;
                } else {
                    tempHeadersOnClick = headers;
                    tempListChildrenOnClick = listChildren;
                }

                JSONObject json = null;
                try {
                    json = new JSONObject(tempListChildrenOnClick.get(tempHeadersOnClick.get(groupPosition)).get(childPosition));
                } catch (JSONException e) {

                }

                if (json != null) {
                    Intent intent = new Intent(getApplicationContext(), LogExpActivity.class);
                    intent.putExtra("json",json.toString());
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");
        payloadAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        // Unregister receiver
        getApplicationContext().unregisterReceiver(logBroadcastReceiver);
        super.onDestroy();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.clear:

                if (isJsonView) {
                    tempHeaders.clear();
                    tempListChildren.clear();
                } else {
                    headers.clear();
                    listChildren.clear();
                }
                payloadAdapter.notifyDataSetChanged();

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
