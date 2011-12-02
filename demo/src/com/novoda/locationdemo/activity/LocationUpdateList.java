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

package com.novoda.locationdemo.activity;

import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.location.LocationFinder;
import com.novoda.location.LocationSettings;
import com.novoda.locationdemo.LocationDemo;
import com.novoda.locationdemo.R;
import com.novoda.locationdemo.analytics.Analytics;

public class LocationUpdateList extends RoboActivity {

    @InjectView(R.id.val_use_gps) TextView useGps;
    @InjectView(R.id.val_updates) TextView updates;
    @InjectView(R.id.val_passive_updates) TextView passive;
    @InjectView(R.id.val_update_interval) TextView interval;
    @InjectView(R.id.val_update_distance) TextView distance;
    @InjectView(R.id.val_passive_interval) TextView passiveInterval;
    @InjectView(R.id.val_passive_distance) TextView passiveDistance;

    private LocationFinder locationFinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        // Get the reference to the locator object.
        LocationDemo app = (LocationDemo) getApplication();
        locationFinder = app.getLocator();

        displayLocationSettings();
        
        new Analytics(this).trackMapTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register broadcast receiver and start location updates.
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationDemo.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, filter);
        locationFinder.startLocationUpdates();
    }

    @Override
    public void onPause() {
        // Unregister broadcast receiver and stop location updates.
        unregisterReceiver(freshLocationReceiver);
        locationFinder.stopLocationUpdates();
        super.onPause();
    }

    public BroadcastReceiver freshLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the fresh location and do something with it...
            displayNewLocation(locationFinder.getLocation());
        }
    };

    private void displayLocationSettings() {
        LocationSettings settings = locationFinder.getSettings();

        useGps.setText(getBooleanText(settings.shouldUseGps()));
        updates.setText(getBooleanText(settings.shouldUpdateLocation()));
        passive.setText(getBooleanText(settings.shouldEnablePassiveUpdates()));
        interval.setText(settings.getUpdatesInterval() / (60 * 1000) + " mins");
        distance.setText(settings.getUpdatesDistance() + "m");
        passiveInterval.setText(settings.getPassiveUpdatesInterval() / (60 * 1000) + " mins");
        passiveDistance.setText(settings.getPassiveUpdatesDistance() + "m");
    }

    private void displayNewLocation(final Location location) {
        View block = getLayoutInflater().inflate(R.layout.location_table, null);

        TextView time = (TextView) block.findViewById(R.id.val_time);
        TextView accuracy = (TextView) block.findViewById(R.id.val_acc);
        TextView provider = (TextView) block.findViewById(R.id.val_prov);
        TextView latitude = (TextView) block.findViewById(R.id.val_lat);
        TextView longitude = (TextView) block.findViewById(R.id.val_lon);

        time.setText(DateFormat.format("hh:mm:ss", new Date(location.getTime())));
        accuracy.setText(location.getAccuracy() + "m");
        provider.setText(location.getProvider());
        latitude.setText("" + location.getLatitude());
        longitude.setText("" + location.getLongitude());

        String providerName = location.getProvider();
        if (providerName.equalsIgnoreCase("network")) {
            block.setBackgroundResource(R.color.network);
        } else if (providerName.equalsIgnoreCase("gps")) {
            block.setBackgroundResource(R.color.gps);
        }
        
        block.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(MapTracking.getIntent(LocationUpdateList.this, location));
			}
		});
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        viewGroup.addView(block);
    }

    private String getBooleanText(boolean bool) {
        return bool ? "ON" : "OFF";
    }

}