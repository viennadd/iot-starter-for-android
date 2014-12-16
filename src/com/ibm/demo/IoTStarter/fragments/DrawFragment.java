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
package com.ibm.demo.IoTStarter.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ibm.demo.IoTStarter.views.DrawingView;
import com.ibm.demo.IoTStarter.IoTStarterApplication;
import com.ibm.demo.IoTStarter.R;
import com.ibm.demo.IoTStarter.utils.Constants;

/**
 * Created by Michael on 12/15/2014.
 */
public class DrawFragment extends IoTStarterFragment {
    private final static String TAG = DrawFragment.class.getName();
    private DrawingView drawingView;

    /**************************************************************************
     * Activity functions for establishing the activity
     **************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.draw, container, false);
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getActivity().getApplication();

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering drawBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for drawBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_DRAW));

        // initialise
        initializeIoTActivity();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeIoTActivity() {
        Log.d(TAG, ".initializeDrawFragment() entered");

        context = getActivity().getApplicationContext();
        drawingView = (DrawingView) getActivity().findViewById(R.id.drawing);
        drawingView.setContext(context);
        drawingView.colorBackground(app.getColor());
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            // Do nothing
        }
        super.onDestroy();
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
        if (data.equals(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Updating background color");
        }
    }
}
