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
package com.ibm.robertmp.IoTStarter.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.ibm.robertmp.IoTStarter.R;

import java.util.HashMap;
import java.util.List;

/**
 * List adapter for LogExpActivity. Allows for expanding of JSON message data.
 */
public class PayloadAdapter extends BaseExpandableListAdapter {

    private Context ctx;
    private List<String> listHeaders;
    private HashMap<String, List<String>> listChildren;

    //initialize adapter with payload data source(HashMap)
    public PayloadAdapter(Context context, List<String> listHeaders,
                          HashMap<String, List<String>> listChildren) {
        this.ctx = context;
        this.listHeaders = listHeaders;
        this.listChildren = listChildren;
    }

    @Override
    public int getGroupCount() {
        return listHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       return listChildren.get(listHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return listHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChildren.get(listHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //Renders topics of messages received in the listView
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        String [] tempTitle = headerTitle.split(":");
        headerTitle = tempTitle[0];
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    //Renders payload data associated with topic and analyses JSONObject payloads ina heirarchical structure
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


