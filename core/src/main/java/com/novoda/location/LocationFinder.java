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
 * 
 * This code is based on Reto Meier's Location Pro-tips and Stefano 
 * Dacchille's IgnitedLocation.
 */

package com.novoda.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.novoda.location.listener.BetterProviderListener;
import com.novoda.location.provider.LocationProviderFactory;
import com.novoda.location.provider.LocationUpdateManager;
import com.novoda.location.util.Log;

public class LocationFinder {

	private static LocationFinder instance; 
    private Context context;
    private LocationSettings settings;
    private volatile Location currentLocation;
    private LocationManager locationManager;
    private Criteria criteria;
    private BetterProviderListener bestInactiveLocationProviderListener;
    private LocationUpdateManager locationUpdateManager;
    
    private LocationFinder() {
    }
    
    public static LocationFinder getInstance() {
    	if(instance == null)  {
    		instance = new LocationFinder();
    	}
    	return instance;
    }
    
    public void prepare(Context c, LocationSettings settings) {
    	Log.v("preparing settings");
        this.context = c;
        this.settings = settings;
        this.locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLocation() {
        return currentLocation;
    }

    public void setLocation(Location location) {
    	Log.v("setLocation");
        if (currentLocation != null && !isValidAndAccurateLocation(location)) {
        	Log.v("location is not valid or is not accurate");
            return;
        }
        currentLocation = location;
        sendLocationUpdateBroadcast();
    }
    
    public LocationSettings getSettings() {
        return settings;
    }

    public void startLocationUpdates() {
    	Log.v("startLocationUpdates");
        createActiveUpdateCriteria();
        createLocationUpdateSender();
        persistSettingsToPreferences();
        sendFirstAvailableLocation();
        startListeningForLocationUpdates();
    }
    
	public void stopLocationUpdates() {
        if (!settings.shouldUpdateLocation()) {
            return;
        }
        stopListeningForLocationUpdates();
    }

	private void persistSettingsToPreferences() {
		new LocationProviderFactory().getSettingsDao().persistSettingsToPreferences(context, settings);
	}
    
	private void sendFirstAvailableLocation() {
		if (currentLocation == null) {
			locationUpdateManager.fetchLastKnownLocation(context);
            return;
        }
		sendLocationUpdateBroadcast();
	}
    
    private void startListeningForLocationUpdates() {
    	if (!settings.shouldUpdateLocation()) {
    		return;
    	}
    	locationUpdateManager.requestActiveLocationUpdates();
        registerProviderDisabledReceiver(context);
        requestAccurateProvider(context);
    }

	private void requestAccurateProvider(Context context) {
		String bestProvider = locationManager.getBestProvider(criteria, false);
        String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
            bestInactiveLocationProviderListener = new BetterProviderListener() {
				@Override
				public void onProviderEnabled(String provider) {
					startListeningForLocationUpdates();
				}
            };
            locationManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener,
                    context.getMainLooper());
        }
        locationUpdateManager.removePassiveUpdates();
	}

	private void registerProviderDisabledReceiver(Context c) {
		c.registerReceiver(registerDisabledProviderReceiver, Constants.PROVIDER_DISABLED_INTENT_FILTER);
	}

    private void stopListeningForLocationUpdates() {
        context.unregisterReceiver(registerDisabledProviderReceiver);
        locationUpdateManager.removeUpdates();
        if (bestInactiveLocationProviderListener != null) {
            locationManager.removeUpdates(bestInactiveLocationProviderListener);
        }
        locationUpdateManager.stopFecthLastKnownLocation();
        locationUpdateManager.requestPassiveLocationUpdates();
    }
    

    private boolean isValidAndAccurateLocation(Location location) {
        long newTime = location.getTime();
        long currentTime = currentLocation.getTime();
        float newAccuracy = location.getAccuracy();
        float currentAccuracy = currentLocation.getAccuracy();
        return !(withinThreshold(newTime, currentTime) && isNewAccuracyWorse(newAccuracy, currentAccuracy));
    }

    private boolean isNewAccuracyWorse(float newAccuracy, float currentAccuracy) {
        return newAccuracy > currentAccuracy;
    }

    private boolean withinThreshold(long newTime, long currentTime) {
        return (newTime - currentTime) < (20 * 1000);
    }

    private void sendLocationUpdateBroadcast() {
    	Log.v("LocationFinder sending update broadcast");
        if (context == null) {
        	Log.v("LocationFinder sending update broadcast but context is null");
        	return;
        }
        Intent broadcast = new Intent();
        broadcast.setAction(settings.getUpdateAction());
        broadcast.setPackage(settings.getPackageName());
        context.sendBroadcast(broadcast);
        Log.v("LocationFinder broadcast sent");
    }

	private void createLocationUpdateSender() {
		this.locationUpdateManager = new LocationUpdateManager(settings, 
				criteria, context, locationManager);
	}
	
    private void createActiveUpdateCriteria() {
        criteria = new Criteria();
        if (settings.shouldUseGps()) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        } else {
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }
    }
    
    private BroadcastReceiver registerDisabledProviderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isProviderEnabled(intent)) {
            	return;
            }
            startListeningForLocationUpdates();
        }

    };
    
    private boolean isProviderEnabled(Intent intent) {
    	return intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
    }

}