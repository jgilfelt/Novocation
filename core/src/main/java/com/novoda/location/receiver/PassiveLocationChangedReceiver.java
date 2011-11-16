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

import com.novoda.location.core.LocationFinder;
import com.novoda.location.location.LegacyLastLocationFinder;
import com.novoda.location.util.Util;

/**
 * This Receiver class is used to listen for Broadcast Intents that announce
 * that a location change has occurred while this application isn't visible.
 * 
 * Where possible, this is triggered by a Passive Location listener.
 */
public class PassiveLocationChangedReceiver extends BroadcastReceiver {

    /**
     * When a new location is received, extract it from the Intent and use it to
     * start the Service used to update the list of nearby places.
     * 
     * This is the Passive receiver, used to receive Location updates from third
     * party apps when the Activity is not visible.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        
        String key = LocationManager.KEY_LOCATION_CHANGED;
        LocationFinder novoLoc = LocationFinder.getInstance();
        Location location = null;

        if (intent.hasExtra(key)) {
            // This update came from Passive provider, so we can extract the
            // location directly.
            location = (Location) intent.getExtras().get(key);
        } else {
            // This update came from a recurring alarm. We need to determine if
            // there has been a more recent Location received than the last
            // location we used.

            long locationUpdateInterval = Util.getPassiveLocationInterval(context);
            int locationUpdateDistanceDiff = Util.getPassiveLocationDistance(context);

            // Get the best last location detected from the providers.
            LegacyLastLocationFinder lastLocationFinder = new LegacyLastLocationFinder(context);
            location = lastLocationFinder.getLastBestLocation(locationUpdateDistanceDiff, System.currentTimeMillis()
                    - locationUpdateInterval);

            // Check if the last location detected from the providers is either
            // too soon, or too close to the last value we used. If it is within
            // those thresholds we set the location to null to prevent the
            // update Service being run unnecessarily (and spending battery on
            // data transfers).
            Location currentLocation = novoLoc.getLocation();
            if ((currentLocation != null && currentLocation.getTime() > System.currentTimeMillis() - locationUpdateInterval)
                    || (currentLocation.distanceTo(location) < locationUpdateDistanceDiff))
                location = null;
        }

        if (location != null) {
            novoLoc.setLocation(location);
        }
    }
}