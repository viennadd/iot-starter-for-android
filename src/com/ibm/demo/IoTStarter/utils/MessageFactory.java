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
package com.ibm.demo.IoTStarter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.ibm.demo.IoTStarter.IoTStarterApplication;

/**
 * Build messages to be published by the application.
 * This class is currently unused.
 */
public class MessageFactory {

    private final static String TAG = MessageFactory.class.getName();
    private static MessageFactory instance;
    private Context context;
    private IoTStarterApplication app;

    private MessageFactory(Context context) {
        this.context = context;
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    public static MessageFactory getInstance(Context context) {
        if (instance == null) {
            instance = new MessageFactory(context);
        }
        return instance;
    }

}
