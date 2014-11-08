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

/**
 * Created by mprobert on 9/29/2014.
 */
public class Constants {

    public final static String APP_ID = "com.ibm.demo.IoTStarter";
    public final static String SETTINGS = APP_ID+".Settings";

    public final static String SETTINGS_MQTT_SERVER = "messaging.internetofthings.ibmcloud.com";
    public final static String SETTINGS_MQTT_PORT = "1883";
    public final static String SETTINGS_USERNAME = "use-token-auth";

    public enum ActionStateStatus {
        CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED, SUBSCRIBE, UNSUBSCRIBE, PUBLISH
    }

    public final static String AUTH_TOKEN = "authtoken";
    public final static String DEVICE_ID = "deviceid";
    public final static String ORGANIZATION = "organization";

    public final static String HOSTNAME = "https://internetofthings.ibmcloud.com:443/api/v0001/organizations/";
    public final static String DEVICE_TYPE = "Android";

    public final static String EVENT_TOPIC = "iot-2/evt/";
    public final static String COMMAND_TOPIC = "iot-2/cmd/";
    public final static String FORMAT_TOPIC = "/fmt/json";

    public final static String ACCEL_EVENT = "accel";
    public final static String COLOR_EVENT = "color";
    public final static String TOUCH_EVENT = "touchmove";
    public final static String LIGHT_EVENT = "light";
    public final static String TEXT_EVENT = "text";
    public final static String ALERT_EVENT = "alert";

    public final static String CONNECTIVITY_MESSAGE = "connectivityMessage";
    public final static String ACTION_INTENT_CONNECTIVITY_MESSAGE_RECEIVED = Constants.APP_ID + "." + "CONNECTIVITY_MESSAGE_RECEIVED";

    public final static String INTENT_LOGIN = "INTENT_LOGIN";
    public final static String INTENT_IOT = "INTENT_IOT";
    public final static String INTENT_LOG = "INTENT_LOG";

    public final static String INTENT_DATA = "data";

    public final static String INTENT_DATA_CONNECT = "connect";
    public final static String INTENT_DATA_DISCONNECT = "disconnect";
    public final static String INTENT_DATA_PUBLISHED = "publish";
    public final static String INTENT_DATA_RECEIVED = "receive";
    public final static String INTENT_DATA_MESSAGE = "message";

    public final static int ERROR_BROKER_UNAVAILABLE = 3;

    public final static int LOCATION_MIN_TIME = 30000;
    public final static float LOCATION_MIN_DISTANCE = 5;
}
