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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ibm.demo.IoTStarter.IoTStarterApplication;
import com.ibm.demo.IoTStarter.R;
import com.ibm.demo.IoTStarter.utils.Constants;
import com.ibm.demo.IoTStarter.utils.DeviceSensor;
import com.ibm.demo.IoTStarter.utils.MqttHandler;

/**
 * The login activity of the IoTStarter application. Provides functionality for
 * connecting to IoT. Also displays device information.
 */
public class LoginActivity extends Activity {
    private final static String TAG = LoginActivity.class.getName();
    private Context context;
    private IoTStarterApplication app;
    private BroadcastReceiver loginBroadcastReceiver;

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

        setContentView(R.layout.login);
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getApplication();
        app.setCurrentRunningActivity(TAG);

        if (loginBroadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering loginBroadcastReceiver");
            loginBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for loginBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getApplicationContext().registerReceiver(loginBroadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_LOGIN));

        // initialise
        initializeLoginActivity();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        getApplicationContext().unregisterReceiver(loginBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeLoginActivity() {
        Log.d(TAG, ".initializeLoginActivity() entered");

        context = getApplicationContext();

        updateViewStrings();

        // setup button listeners
        initializeButtons();
    }

    /**
     * Update strings in the activity based on IoTStarterApplication values.
     */
    private void updateViewStrings() {
        // Update only if the organization is set to some non-empty string.
        if (app.getOrganization() != null) {
            ((EditText) findViewById(R.id.organizationValue)).setText(app.getOrganization());
        }

        // DeviceId should never be null at this point.
        if (app.getDeviceId() != null) {
            ((EditText) findViewById(R.id.deviceIDValue)).setText(app.getDeviceId());
        }

        if (app.getAuthToken() != null) {
            ((EditText) findViewById(R.id.authTokenValue)).setText(app.getAuthToken());
        }

        // Set 'Connected to IoT' to Yes if MQTT client is connected. Leave as No otherwise.
        if (app.isConnected()) {
            processConnectIntent();
        }
    }

    /**
     * Setup listeners for buttons.
     */
    private void initializeButtons() {
        Log.d(TAG, ".initializeButtons() entered");
        Button button = (Button) findViewById(R.id.activateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleActivate();
            }
        });
    }

    /**************************************************************************
     * Functions to handle button presses
     **************************************************************************/

    /**
     * Check whether the required properties are set for the app to connect to IoT.
     *
     * @return True if properties are set, false otherwise.
     */
    private boolean checkCanConnect() {
        if (app.getOrganization() == null || app.getOrganization().equals("") ||
                app.getDeviceId() == null || app.getDeviceId().equals("") ||
                app.getAuthToken() == null || app.getAuthToken().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Display alert dialog indicating what properties must be set in order to connect to IoT.
     */
    private void displaySetPropertiesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unable to connect")
                .setMessage("Organization ID, Device ID and Auth Token must be set in order to connect.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    /**
     * If button is currently 'Activate', then connect the MQTT client.
     * If button is currently 'Deactivate', then disconnect the MQTT client.
     */
    private void handleActivate() {
        Log.d(TAG, ".handleActivate() entered");
        String buttonTitle = ((Button) findViewById(R.id.activateButton)).getText().toString();
        MqttHandler mqttHandle = MqttHandler.getInstance(context);
        Button activateButton = (Button) findViewById(R.id.activateButton);
        app.setDeviceId(((EditText) findViewById(R.id.deviceIDValue)).getText().toString());
        app.setOrganization(((EditText) findViewById(R.id.organizationValue)).getText().toString());
        app.setAuthToken(((EditText) findViewById(R.id.authTokenValue)).getText().toString());
        activateButton.setEnabled(false);
        if (buttonTitle.equals(getResources().getString(R.string.activate_button)) && app.isConnected() == false) {
            if (checkCanConnect()) {
                mqttHandle.connect();
            } else {
                displaySetPropertiesDialog();
                activateButton.setEnabled(true);
            }
        } else if (buttonTitle.equals(getResources().getString(R.string.deactivate_button)) && app.isConnected() == true) {
            mqttHandle.disconnect();
        }
    }

    /**************************************************************************
     * Functions to process intent broadcasts from other classes
     **************************************************************************/

    /**
     * Process the incoming intent broadcast.
     *
     * @param intent The intent which was received by the activity.
     */
    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");
        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.INTENT_DATA_CONNECT)) {
            processConnectIntent();
            openIoT();
        } else if (data.equals(Constants.INTENT_DATA_DISCONNECT)) {
            processDisconnectIntent();
        }
    }

    /**
     * Intent data contained INTENT_DATA_CONNECT.
     * Update Connected to Yes.
     */
    private void processConnectIntent() {
        Log.d(TAG, ".processConnectIntent() entered");
        Button activateButton = (Button) findViewById(R.id.activateButton);
        activateButton.setEnabled(true);
        String connectedString = this.getString(R.string.isConnected);
        connectedString = connectedString.replace("No", "Yes");
        ((TextView) findViewById(R.id.isConnected)).setText(connectedString);
        activateButton.setText(getResources().getString(R.string.deactivate_button));
        if (app.isAccelEnabled()) {
            app.setDeviceSensor(DeviceSensor.getInstance(context));
            app.getDeviceSensor().enableSensor();
        }
    }

    /**
     * Intent data contained INTENT_DATA_DISCONNECT.
     * Update Connected to No.
     */
    private void processDisconnectIntent() {
        Log.d(TAG, ".processDisconnectIntent() entered");
        Button activateButton = (Button) findViewById(R.id.activateButton);
        activateButton.setEnabled(true);
        ((TextView) findViewById(R.id.isConnected)).setText(this.getString(R.string.isConnected));
        activateButton.setText(getResources().getString(R.string.activate_button));
        if (app.getDeviceSensor() != null && app.isAccelEnabled()) {
            app.getDeviceSensor().disableSensor();
        }
    }

    /**************************************************************************
     * Functions to handle the menu bar
     **************************************************************************/

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
     * Infalte the options menu.
     *
     * @param menu The menu to create.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, ".onCreateOptions() entered");
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Process the selected menu item.
     *
     * @param item The selected menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_login:
                return true;
            case R.id.action_iot:
                openIoT();
                return true;
            case R.id.action_log:
                openLog();
                return true;
            case R.id.action_accel:
                app.toggleAccel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}