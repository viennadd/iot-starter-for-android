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
package com.ibm.robertmp.IoTStarter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ibm.robertmp.IoTStarter.DrawingView;
import com.ibm.robertmp.IoTStarter.IoTStarterApplication;
import com.ibm.robertmp.IoTStarter.R;
import com.ibm.robertmp.IoTStarter.utils.Constants;
import com.ibm.robertmp.IoTStarter.utils.MqttHandler;
import com.ibm.robertmp.IoTStarter.utils.TopicFactory;

/**
 * The IoT Activity is the main activity of the application that will be displayed while the device is connected
 * to IoT. From this activity, users can send touchmove and text event messages. Users can also see the number
 * of messages the device has published and received while connected.
 */
public class IoTActivity extends Activity {
    private final static String TAG = IoTActivity.class.getName();
    private Context context;
    private IoTStarterApplication app;
    private DrawingView drawingView;
    private BroadcastReceiver iotBroadcastReceiver;

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

        setContentView(R.layout.iot);
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

        if (iotBroadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering iotBroadcastReceiver");
            iotBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for iotBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getApplicationContext().registerReceiver(iotBroadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_IOT));

        // initialise
        initializeIoTActivity();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        getApplicationContext().unregisterReceiver(iotBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeIoTActivity() {
        Log.d(TAG, ".initializeIoTActivity() entered");

        context = getApplicationContext();
        drawingView = (DrawingView) findViewById(R.id.drawing);
        drawingView.setContext(context);
        drawingView.colorBackground(app.getColor());

        updateViewStrings();

        // setup button listeners
        Button button = (Button) findViewById(R.id.sendText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendText();
            }
        });
    }

    /**
     * Update strings in the activity based on IoTStarterApplication values.
     */
    private void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        // DeviceId should never be null at this point.
        if (app.getDeviceId() != null) {
            ((TextView) findViewById(R.id.deviceIDIoT)).setText(app.getDeviceId());
        } else {
            ((TextView) findViewById(R.id.deviceIDIoT)).setText("-");
        }

        // Update publish count view.
        processPublishIntent();

        // Update receive count view.
        processReceiveIntent();
    }

    /**************************************************************************
     * Functions to handle button presses
     **************************************************************************/

    /**
     * Handle pressing of the send text button. Prompt the user to enter text
     * to send.
     */
    private void handleSendText() {
        Log.d(TAG, ".handleSendText() entered");
        final EditText input = new EditText(context);
        new AlertDialog.Builder(this)
                .setTitle("Send Text Message")
                .setMessage("Input message text to send.")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String messageData = "{\"d\":{" +
                                "\"deviceId\":\"" + app.getDeviceId() + "\", " +
                                "\"deviceType\":\"Android\", " +
                                "\"type\":\"text\", " +
                                "\"text\":\"" + value.toString() + "\"" +
                                " } }";
                        MqttHandler mqtt = MqttHandler.getInstance(context);
                        mqtt.publish(TopicFactory.getEventTopic(Constants.TEXT_EVENT), messageData, false, 0);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    /**************************************************************************
     * Functions to process intent broadcasts from other classes
     **************************************************************************/

    /**
     * Process the incoming intent broadcast.
     * @param intent The intent which was received by the activity.
     */
    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");
        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.INTENT_DATA_PUBLISHED)) {
            processPublishIntent();
        } else if (data.equals(Constants.INTENT_DATA_RECEIVED)) {
            processReceiveIntent();
        } else if (data.equals(Constants.ACCEL_EVENT)) {
            processAccelEvent();
        } else if (data.equals(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Updating background color");
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(app.getColor());
            drawingView.colorBackground(app.getColor());
            ((LinearLayout) findViewById(R.id.iotRoot)).setBackgroundColor(app.getColor());
        }
    }

    /**
     * Intent data contained INTENT_DATA_PUBLISH
     * Update the published messages view based on app.getPublishCount()
     */
    private void processPublishIntent() {
        Log.v(TAG, ".processPublishIntent() entered");
        String publishedString = this.getString(R.string.messagesPublished);
        publishedString = publishedString.replace("0",Integer.toString(app.getPublishCount()));
        ((TextView) findViewById(R.id.messagesPublishedView)).setText(publishedString);
    }

    /**
     * Intent data contained INTENT_DATA_RECEIVE
     * Update the received messages view based on app.getReceiveCount();
     */
    private void processReceiveIntent() {
        Log.v(TAG, ".processReceiveIntent() entered");
        String receivedString = this.getString(R.string.messagesReceived);
        receivedString = receivedString.replace("0",Integer.toString(app.getReceiveCount()));
        ((TextView) findViewById(R.id.messagesReceivedView)).setText(receivedString);
    }

    /**
     * Update acceleration view strings
     */
    private void processAccelEvent() {
        Log.v(TAG, ".processAccelEvent()");
        float[] accelData = app.getAccelData();
        ((TextView) findViewById(R.id.accelX)).setText("x: " + accelData[0]);
        ((TextView) findViewById(R.id.accelY)).setText("y: " + accelData[1]);
        ((TextView) findViewById(R.id.accelZ)).setText("z: " + accelData[2]);
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
     * Switch to the Log activity.
     */
    private void openLog() {
        Log.d(TAG, ".openLog() entered");
        Intent logIntent = new Intent(getApplicationContext(), LogExpActivity.class);
        startActivity(logIntent);
    }

    /**
     * Infalte the options menu.
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
     * @param item The selected menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_login:
                openLogin();
                return true;
            case R.id.action_iot:
                return true;
            case R.id.action_log:
                openLog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
