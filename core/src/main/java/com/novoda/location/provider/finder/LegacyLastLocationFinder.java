/*
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
 */

package com.novoda.location.provider.finder;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.novoda.location.provider.LastLocationFinder;

/**
 * Legacy implementation of Last Location Finder for all Android platforms down
 * to Android 1.6.
 */
public class LegacyLastLocationFinder implements LastLocationFinder {

	protected final Context context;
	protected final LocationManager locationManager;
	protected final Criteria criteria = new Criteria();
	
	protected LocationListener locationListener;

	public LegacyLastLocationFinder(LocationManager locationManager, Context context) {
		this.context = context;
		this.locationManager = locationManager;
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	}
	   
	@Override
	public Location getLastBestLocation(int minDistance, long minTime) {
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MAX_VALUE;

		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time < minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time > minTime && bestAccuracy == Float.MAX_VALUE
						&& time < bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		// If the best result is beyond the allowed time limit, or the accuracy of the
		// best result is wider than the acceptable maximum distance, request a single update.
		// This check simply implements the same conditions we set when requesting regular
		// location updates every [minTime] and [minDistance].
		// Prior to Gingerbread "one-shot" updates weren't available, so we need to implement
		// this manually.
		if (locationListener != null && (bestTime > minTime || bestAccuracy > minDistance)) {
			String provider = locationManager.getBestProvider(criteria, true);
			if (provider != null){
			    locationManager.requestLocationUpdates(provider, 0, 0, singeUpdateListener, context.getMainLooper());
			}
		}
		return bestResult;
	}

	protected LocationListener singeUpdateListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (locationListener != null && location != null) {
				locationListener.onLocationChanged(location);
			}
			locationManager.removeUpdates(singeUpdateListener);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		@Override
		public void onProviderEnabled(String provider) {
		}
		@Override
		public void onProviderDisabled(String provider) {
		}
	};

	@Override
	public void setChangedLocationListener(LocationListener l) {
		locationListener = l;
	}

	@Override
	public void cancel() {
		locationManager.removeUpdates(singeUpdateListener);
	}

}
