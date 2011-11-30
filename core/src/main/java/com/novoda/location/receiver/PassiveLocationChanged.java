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
 * 
 * Code modified by Novoda, 2011.
 */

package com.novoda.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.novoda.location.LocationFinder;
import com.novoda.location.provider.LocationProviderFactory;
import com.novoda.location.provider.finder.LegacyLastLocationFinder;
import com.novoda.location.provider.store.SettingsDao;
import com.novoda.location.util.Log;

public class PassiveLocationChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.v("PassiveLocationChanged onReceive");
        String key = LocationManager.KEY_LOCATION_CHANGED;
        Location location = null;
        if (intent.hasExtra(key)) {
            location = (Location) intent.getExtras().get(key);
            updateLocation(location);
            return;
        } else {
            // This update came from a recurring alarm. We need to determine if
            // there has been a more recent Location received than the last
            // location we used.
        	SettingsDao settings = new LocationProviderFactory().getSettingsDao();
            long locationUpdateInterval = settings.getPassiveLocationInterval(context);
            int locationUpdateDistanceDiff = settings.getPassiveLocationDistance(context);
            // Get the best last location detected from the providers.
            
            long delta = System.currentTimeMillis() - locationUpdateInterval;
            
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            LegacyLastLocationFinder lastLocationFinder = new LegacyLastLocationFinder(locationManager, context);
            location = lastLocationFinder.getLastBestLocation(locationUpdateDistanceDiff, delta);
            // Check if the last location detected from the providers is either
            // too soon, or too close to the last value we used. If it is within
            // those thresholds we set the location to null to prevent the
            // update Service being run unnecessarily (and spending battery on
            // data transfers).
            verifyAndUpdateLocation(location, locationUpdateDistanceDiff, delta);
        }
        
        
    }

	private void verifyAndUpdateLocation(Location location, int locationUpdateDistanceDiff, long delta) {
		Location currentLocation = LocationFinder.getInstance().getLocation();
		if ((currentLocation != null && currentLocation.getTime() > delta)
		        || (currentLocation.distanceTo(location) < locationUpdateDistanceDiff)) {
			return;
		}
		if (location == null) {
			return;
		}
		LocationFinder.getInstance().setLocation(location);
	}

	private void updateLocation(Location location) {
		if (location == null) {
			return;
		}
		LocationFinder.getInstance().setLocation(location);
	}
}