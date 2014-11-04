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
package com.ibm.robertmp.IoTStarter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ibm.robertmp.IoTStarter.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main class for the IoTStarter application. Stores values for
 * important device information.
 */
public class IoTStarterApplication extends Application {

    private String currentRunningActivity;
    private String organization;
    private String deviceId;
    private String authToken;
    private SharedPreferences settings;
    private final static String TAG = IoTStarterApplication.class.getName();

    private boolean connected = false;
    private int publishCount = 0;
    private int receiveCount = 0;

    private int color = Color.WHITE;

    private Camera camera;

    private boolean isCameraOn = false;
    private float[] accelData;

    private ArrayList<String> messageLog;
    //payload being received are stored here
    public HashMap<String, List<String>> payload = new HashMap<String, List<String>>();
    public List<String> topicsReceived = new ArrayList<String>(); //list of payload topics

    /**
     * Called when the application is created. Initializes the application.
     */
    @Override
    public void onCreate() {
        Log.d(TAG, ".onCreate() entered");
        super.onCreate();

        settings = getSharedPreferences(Constants.SETTINGS, 0);
        this.setOrganization(settings.getString(Constants.ORGANIZATION, ""));
        this.setDeviceId(settings.getString(Constants.DEVICE_ID, ""));
        this.setAuthToken(settings.getString(Constants.AUTH_TOKEN, ""));
        Log.d(TAG, "auth token: " + this.authToken);

        messageLog = new ArrayList<String>();
    }

    /**
     * Turn flashlight on or off when a light command message is received.
     */
    public void handleLightMessage() {
        Log.d(TAG, ".handleLightMessage() entered");
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            if (!isCameraOn) {
                Log.d(TAG, "FEATURE_CAMERA_FLASH true");
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                isCameraOn = true;
            } else {
                camera.stopPreview();
                camera.release();
                isCameraOn = false;
            }
        } else {
            Log.d(TAG, "FEATURE_CAMERA_FLASH false");
        }
    }

    public String getCurrentRunningActivity() { return currentRunningActivity; }

    public void setCurrentRunningActivity(String currentRunningActivity) { this.currentRunningActivity = currentRunningActivity; }

    public void setCurrentRunningActivityEmpty() {
        this.currentRunningActivity = null;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.ORGANIZATION, organization);
        editor.commit();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.DEVICE_ID, deviceId);
        editor.commit();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.AUTH_TOKEN, authToken);
        editor.commit();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(int publishCount) {
        this.publishCount = publishCount;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float[] getAccelData() { return accelData; };

    public void setAccelData(float[] accelData) {
        this.accelData = accelData.clone();
    }

    public ArrayList<String> getMessageLog() {
        return messageLog;
    }

    public void setMessageLog(ArrayList<String> messageLog) {
        this.messageLog = messageLog;
    }

    public HashMap<String, List<String>> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<String, List<String>> payload) {
        this.payload = payload;
    }

    public List<String> getTopicsReceived() {
        return topicsReceived;
    }

    public void setTopicsReceived(List<String> topicsReceived) {
        this.topicsReceived = topicsReceived;
    }
}
