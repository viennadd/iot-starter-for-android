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
package com.ibm.robertmp.IoTStarter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import com.ibm.robertmp.IoTStarter.IoTStarterApplication;
import com.ibm.robertmp.IoTStarter.activities.IoTActivity;
import com.ibm.robertmp.IoTStarter.activities.LogActivity;
import com.ibm.robertmp.IoTStarter.activities.LogExpActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Steer incoming MQTT messages to the proper activities based on their content.
 */
public class MessageConductor {

    private final static String TAG = MessageConductor.class.getName();
    private static MessageConductor instance;
    private Context context;
    private IoTStarterApplication app;

    private MessageConductor(Context context) {
        this.context = context;
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    public static MessageConductor getInstance(Context context) {
        if (instance == null) {
            instance = new MessageConductor(context);
        }
        return instance;
    }

    /**
     * Steer incoming MQTT messages to the proper activities based on their content.
     *
     * @param payload The log of the MQTT message.
     * @param topic The topic the MQTT message was received on.
     * @throws JSONException If the message contains invalid JSON.
     */
    public void steerMessage(String payload, String topic) throws JSONException {
        Log.d(TAG, ".steerMessage() entered");
        JSONObject top = new JSONObject(payload);
        JSONObject d = top.getJSONObject("d");
        String deviceID = d.getString("deviceId");
        Log.v(TAG, "DeviceID from message: " + deviceID);

        if (topic.contains(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Color Event");
            int r = d.getInt("r");
            int g = d.getInt("g");
            int b = d.getInt("b");
            // alpha value received is 0.0 < a < 1.0 but Color.agrb expects 0 < a < 255
            int alpha = (int)(d.getDouble("alpha")*255.0);

            app.setColor(Color.argb(alpha, r, g, b));

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null && runningActivity.equals(IoTActivity.class.getName())) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.COLOR_EVENT);
                context.sendBroadcast(actionIntent);
            }
        } else if (topic.contains(Constants.LIGHT_EVENT)) {
            app.handleLightMessage();
        } else if (topic.contains(Constants.TEXT_EVENT)) {
            // app.getMessageLog().add(d.getString("text"));

            // save payload in an arrayList
            List messageRecvd = new ArrayList<String>();
            messageRecvd.add(payload);

            String uniqueTopic = topic+":"+ UUID.randomUUID().toString();

            app.getPayload().put(uniqueTopic, messageRecvd);
            app.getTopicsReceived().add(uniqueTopic);

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null && runningActivity.equals(LogExpActivity.class.getName())) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
                context.sendBroadcast(actionIntent);
            }
        }
    }
}
