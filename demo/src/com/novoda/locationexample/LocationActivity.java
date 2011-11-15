/*
 * Copyright 2011 Novoda Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novoda.locationexample;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.novoda.locationexample.R;
import com.novoda.location.core.NovocationLocator;
import com.novoda.location.core.NovocationSettings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationActivity extends Activity {
    
    private NovocationLocator novocation;
    private ViewGroup viewGroup;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Get the reference to the locator object.
        LocationApplication app = (LocationApplication) getApplication();
        novocation = app.getLocator();
        
        viewGroup = (ViewGroup) findViewById(R.id.content);
        displayLocationSettings();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Register broadcast receiver and start location updates.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, filter);
        novocation.startLocationUpdates();
    }
    
    @Override
    public void onPause() {
        // Unregister broadcast receiver and stop location updates.
        unregisterReceiver(freshLocationReceiver);
        novocation.stopLocationUpdates(true);
        super.onPause();
    }
    
    public BroadcastReceiver freshLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the fresh location and do something with it...
            displayNewLocation(novocation.getLocation());
        }  
    };
    
    private void displayLocationSettings() {
        NovocationSettings settings = novocation.getSettings();
        
        TextView useGps = (TextView) findViewById(R.id.val_use_gps);
        TextView updates = (TextView) findViewById(R.id.val_updates);
        TextView passive = (TextView) findViewById(R.id.val_passive_updates);
        TextView interval = (TextView) findViewById(R.id.val_update_interval);
        TextView distance = (TextView) findViewById(R.id.val_update_distance);
        TextView passiveInterval = (TextView) findViewById(R.id.val_passive_interval);
        TextView passiveDistance = (TextView) findViewById(R.id.val_passive_distance);
        
        useGps.setText(getBooleanText(settings.shouldUseGps()));
        updates.setText(getBooleanText(settings.shouldUpdateLocation()));
        passive.setText(getBooleanText(settings.shouldEnablePassiveUpdates()));
        long updatesMins = TimeUnit.MILLISECONDS.toMinutes(settings.getLocationUpdatesInterval());
        interval.setText(updatesMins + " mins");
        distance.setText(settings.getLocationUpdatesDistanceDiff() + "m");
        long passiveUpdateMins = TimeUnit.MILLISECONDS.toMinutes(settings.getPassiveLocationUpdatesInterval());
        passiveInterval.setText(passiveUpdateMins + " mins");
        passiveDistance.setText(settings.getPassiveLocatoionUpdatesDistanceDiff() + "m");
    }
    
    private void displayNewLocation(Location freshLocation) {
        View block = getLayoutInflater().inflate(R.layout.location_table, null);
        
        TextView time = (TextView) block.findViewById(R.id.val_time);
        TextView accuracy = (TextView) block.findViewById(R.id.val_acc);
        TextView provider = (TextView) block.findViewById(R.id.val_prov);
        TextView latitude = (TextView) block.findViewById(R.id.val_lat);
        TextView longitude = (TextView) block.findViewById(R.id.val_lon);
        
        time.setText(getFormattedTime(freshLocation.getTime()));
        accuracy.setText(freshLocation.getAccuracy() + "m");
        provider.setText(freshLocation.getProvider());
        latitude.setText("" + freshLocation.getLatitude());
        longitude.setText("" + freshLocation.getLongitude());

        viewGroup.addView(block);
    }

    private CharSequence getFormattedTime(long ms) {
        Date locDate = new Date(ms);
        CharSequence formattedTime = DateFormat.format("hh:mm:ss", locDate);
        return formattedTime;
    }
    
    private String getBooleanText(boolean bool) {
        if (bool) {
            return "ON";
        } else {
            return "OFF";
        }
    }

}
