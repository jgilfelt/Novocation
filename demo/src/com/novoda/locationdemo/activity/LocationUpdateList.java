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
import java.util.List;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.novoda.location.LocationFinder;
import com.novoda.location.LocationSettings;
import com.novoda.location.util.Log;
import com.novoda.locationdemo.LocationDemo;
import com.novoda.locationdemo.R;
import com.novoda.locationdemo.activity.location.AccuracyCircleOverlay;
import com.novoda.locationdemo.analytics.Analytics;

public class LocationUpdateList extends RoboMapActivity {

    @InjectView(R.id.val_use_gps) TextView useGps;
    @InjectView(R.id.val_updates) TextView updates;
    @InjectView(R.id.val_passive_updates) TextView passive;
    @InjectView(R.id.val_update_interval) TextView interval;
    @InjectView(R.id.val_update_distance) TextView distance;
    @InjectView(R.id.val_passive_interval) TextView passiveInterval;
    @InjectView(R.id.val_passive_distance) TextView passiveDistance;
    @InjectView(R.id.mapView) MapView mapView;

    //========================================================
    //TODO
    private LocationFinder locationFinder;
    //========================================================
    
    private static final int FEEDBACK_DIALOG = 1;
    
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private long time;
	private Location currentLocation;
	private Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_update_list_activity);

        analytics = new Analytics(this);
        
        //========================================================
        // TODO if you want to use location finder
        // Get the reference to the locator object.
        LocationDemo app = (LocationDemo) getApplication();
        locationFinder = app.getLocator();
        //========================================================
        
        displayLocationSettings();
        mapView.setBuiltInZoomControls(false);
        mapController = mapView.getController();
        mapController.setZoom(17);
        mapOverlays = mapView.getOverlays();        
        analytics.trackLocationUpdateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        
        //========================================================
        // TODO
        // Register broadcast receiver and start location updates.
        IntentFilter f = new IntentFilter();
        f.addAction(LocationDemo.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, f);
        //========================================================
        
        locationFinder.startLocationUpdates();
        time = System.currentTimeMillis();
        currentLocation = null;
    }

    @Override
    public void onPause() {
    	
    	//========================================================
    	// TODO
        // Unregister broadcast receiver and stop location updates.
        unregisterReceiver(freshLocationReceiver);
        locationFinder.stopLocationUpdates();
        //========================================================
        
        analytics.trackLocationSuccessOrFailure(currentLocation, time);
        super.onPause();
    }

	public BroadcastReceiver freshLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	currentLocation = locationFinder.getLocation();
        	new Analytics(context).trackLocationReceived(locationFinder.getLocation(), 
        			currentLocation, time);
            displayNewLocation(locationFinder.getLocation());
            update(locationFinder.getLocation());
        }
    };
    
    protected Dialog onCreateDialog(int id) {
    	if(FEEDBACK_DIALOG == id) {
    		return new AlertDialog.Builder(this)
		          .setTitle("Help us getting better")
		          .setMessage("Did you get a good location quickly?")
		          .setCancelable(true)
		          .setPositiveButton("Yes", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							analytics.trackPositiveFeedback();
							LocationUpdateList.this.finish();
						}
		          })
		          .setNegativeButton("No", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							analytics.trackNegativeFeedback();
							LocationUpdateList.this.finish();
						}
		          }).create();
    	}
    	return super.onCreateDialog(id);
    };
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	showDialog(FEEDBACK_DIALOG);
	    	return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
    private void update(Location location) {
        mapOverlays.clear();
        int lat = (int)(location.getLatitude() * 1E6); 
		int lon = (int)(location.getLongitude() * 1E6);
		final float accuracy = location.getAccuracy();
		GeoPoint point = new GeoPoint(lat, lon);
    	mapController.setCenter(point);
        mapOverlays.add(new AccuracyCircleOverlay(point, accuracy));
    }

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
    	Log.v("Getting <accuracy,latitude,longitude>: " + location.getAccuracy() + " " + location.getLatitude() +
    			" " + location.getLongitude());
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
				update(location);
			}
		});
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        viewGroup.addView(block, 0);
    }

    private String getBooleanText(boolean bool) {
        return bool ? "ON" : "OFF";
    }
	
}
