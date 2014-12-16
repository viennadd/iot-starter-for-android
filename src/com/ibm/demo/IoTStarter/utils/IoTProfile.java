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

import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IoTProfile {
    private String profileName;
    private String organization;
    private String deviceID;
    private String authorizationToken;

    public IoTProfile() {
    }

    public IoTProfile(String profileName, String organization, String deviceID, String authorizationToken) {
        this.profileName = profileName;
        this.organization = organization;
        this.deviceID = deviceID;
        this.authorizationToken = authorizationToken;
    }

    public IoTProfile(Set<String> profileSet) {
        Iterator<String> iter = profileSet.iterator();
        while (iter.hasNext()) {
            String value = iter.next();
            if (value.contains("name:")) {
                this.profileName = value.substring(5);
            } else if (value.contains("org:")) {
                this.organization = value.substring(4);
            } else if (value.contains("deviceId:")) {
                this.deviceID = value.substring(9);
            } else if (value.contains("authToken:")) {
                this.authorizationToken = value.substring(10);
            }
        }
    }

    public Set<String> convertToSet() {
        // Put the new profile into the store settings and remove the old stored properties.
        Set<String> profileSet = new HashSet<String>();
        profileSet.add("name:"+this.profileName);
        profileSet.add("org:" + this.organization);
        profileSet.add("deviceId:" + this.deviceID);
        profileSet.add("authToken:" + this.authorizationToken);

        return profileSet;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
