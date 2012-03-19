/**
 * Copyright 2011 Google Inc.
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
 * 
 * Code modified by Novoda Ltd, 2011.
 */
package com.novoda.location.provider.finder;

import java.util.List;

import com.novoda.location.provider.LastLocationFinder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GingerbreadLastLocationFinder implements LastLocationFinder {

	protected static String SINGLE_LOCATION_UPDATE_ACTION = "com.radioactiveyak.places.SINGLE_LOCATION_UPDATE_ACTION";
	protected static String TAG = "LastLocationFinder";

	protected PendingIntent singleUpatePI;
	protected LocationListener locationListener;
	protected LocationManager locationManager;
	protected Context context;
	protected Criteria criteria;

	public GingerbreadLastLocationFinder(LocationManager locationManager, Context context) {
		this.context = context;
		this.locationManager = locationManager;
		createCriteria();
		createPendingIntent(context);
	}

	private void createCriteria() {
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_LOW);
	}

	private void createPendingIntent(Context context) {
		Intent updateIntent = new Intent(SINGLE_LOCATION_UPDATE_ACTION);
		singleUpatePI = PendingIntent.getBroadcast(context, 0, updateIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public Location getLastBestLocation(int minDistance, long minTime) {
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;

		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();
				if (time > minTime && accuracy < bestAccuracy) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < minTime && bestAccuracy == Float.MAX_VALUE
						&& time > bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		// If the best result is beyond the allowed time limit, or the accuracy
		// of the
		// best result is wider than the acceptable maximum distance, request a
		// single update.
		// This check simply implements the same conditions we set when
		// requesting regular
		// location updates every [minTime] and [minDistance].
		if (locationListener != null
				&& (bestTime < minTime || bestAccuracy > minDistance)) {
			IntentFilter locIntentFilter = new IntentFilter(
					SINGLE_LOCATION_UPDATE_ACTION);
			context.registerReceiver(singleUpdateReceiver, locIntentFilter);
			locationManager.requestSingleUpdate(criteria, singleUpatePI);
		}

		return bestResult;
	}

	protected BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			context.unregisterReceiver(singleUpdateReceiver);
			String key = LocationManager.KEY_LOCATION_CHANGED;
			Location location = (Location) intent.getExtras().get(key);
			if (locationListener != null && location != null) {
				locationListener.onLocationChanged(location);
			}
			locationManager.removeUpdates(singleUpatePI);
		}
	};

	@Override
	public void setChangedLocationListener(LocationListener l) {
		locationListener = l;
	}

	@Override
	public void cancel() {
		locationManager.removeUpdates(singleUpatePI);
		try {
			context.unregisterReceiver(singleUpdateReceiver);
		} catch(Exception e) {
			//In case it has not been unregister in onReceive
		}
	}
}
