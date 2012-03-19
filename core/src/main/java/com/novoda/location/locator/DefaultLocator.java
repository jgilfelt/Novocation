/**
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
 * 
 * Code modified by Novoda Ltd, 2011.
 */
package com.novoda.location.locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.novoda.location.Constants;
import com.novoda.location.Locator;
import com.novoda.location.LocatorSettings;
import com.novoda.location.exception.NoProviderAvailable;
import com.novoda.location.listener.LocationProviderListener;
import com.novoda.location.provider.LocationProviderFactory;
import com.novoda.location.provider.LocationUpdateManager;
import com.novoda.location.util.LocationAccuracy;

public class DefaultLocator implements Locator {

	private LocationAccuracy locationAccuracy = new LocationAccuracy();
	private BroadcastReceiver registerDisabledProviderReceiver;
	private volatile Location currentLocation;
    private Context context;
    private LocatorSettings settings;
    private LocationManager locationManager;
    private Criteria criteria;
    private LocationProviderListener bestLocationListener;
    private LocationProviderListener networkLocationListener;
    private LocationUpdateManager locationUpdateManager;
    
    @Override
    public void prepare(Context c, LocatorSettings settings) {
        this.context = c;
        this.settings = settings;
        this.locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public Location getLocation() {
        return currentLocation;
    }

    @Override
    public void setLocation(Location location) {
        if (locationAccuracy.isWorseLocation(location, currentLocation)) {
            return;
        }
        currentLocation = location;
        sendLocationUpdateBroadcast();
    }
    
    @Override
    public LocatorSettings getSettings() {
        return settings;
    }

    @Override
    public void startLocationUpdates() throws NoProviderAvailable {
        createActiveUpdateCriteria();
        persistSettingsToPreferences();
        
        createLocationUpdateManager();
        sendFirstAvailableLocation();
        
        prepareDisabledProviderReceiver();
        startListeningForLocationUpdates();
    }

    @Override
	public void stopLocationUpdates() {
        if (!settings.shouldUpdateLocation()) {
            return;
        }
        stopListeningForLocationUpdates();
    }
	
    @Override
	public boolean isNetworkProviderEnabled() {
	    return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

    @Override
	public boolean isGpsProviderEnabled() {
	    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private void createLocationUpdateManager() throws NoProviderAvailable {
		this.locationUpdateManager = new LocationUpdateManager(settings, criteria, context, locationManager);
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
    
    private void startListeningForLocationUpdates() throws NoProviderAvailable {
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
        	if(LocationManager.GPS_PROVIDER.equals(bestProvider)) {
        		addNetworkLocationProviderListener(context);
        	}
            addBestLocationProviderListener(context, bestProvider);
        }
        locationUpdateManager.removePassiveUpdates();
	}

	private void addBestLocationProviderListener(Context context, String bestProvider) {
		bestLocationListener = new CustomLocationProviderListener();
		locationManager.requestLocationUpdates(bestProvider, 0, 0, bestLocationListener,
		        context.getMainLooper());
	}

	private void addNetworkLocationProviderListener(Context context) {
		networkLocationListener = new CustomLocationProviderListener();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener,
		        context.getMainLooper());
	}

    private void stopListeningForLocationUpdates() {
        unregisterDisabledProviderReceiver();
        locationUpdateManager.removeUpdates();
        if (bestLocationListener != null) {
            locationManager.removeUpdates(bestLocationListener);
        }
        if(networkLocationListener != null) {
        	locationManager.removeUpdates(networkLocationListener);
        }
        locationUpdateManager.stopFecthLastKnownLocation();
        locationUpdateManager.requestPassiveLocationUpdates();
    }

	private void registerProviderDisabledReceiver(Context c) {
		c.registerReceiver(registerDisabledProviderReceiver, Constants.PROVIDER_DISABLED_INTENT_FILTER);
	}
	
	private void prepareDisabledProviderReceiver() {
		registerDisabledProviderReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (isProviderEnabled(intent)) {
	            	return;
	            }
	            startListeningForLocationUpdatesSilently();
	        }
	    };
	}
	
    private void sendLocationUpdateBroadcast() {
        if (context == null) {
        	return;
        }
        Intent broadcast = new Intent();
        broadcast.setAction(settings.getUpdateAction());
        broadcast.setPackage(settings.getPackageName());
        context.sendBroadcast(broadcast);
    }
	
    private void createActiveUpdateCriteria() {
        criteria = new Criteria();
        if (settings.shouldUseGps()) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        } else {
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }
    }
    
    private boolean isProviderEnabled(Intent intent) {
    	return intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
    }
    
    private class CustomLocationProviderListener extends LocationProviderListener {
		@Override
		public void onProviderEnabled(String provider) {
			startListeningForLocationUpdatesSilently();
		}
	};
	
	private void startListeningForLocationUpdatesSilently() {
		try {
			startListeningForLocationUpdates();
		} catch (NoProviderAvailable npa) {
			
		}
	}
	
	private void unregisterDisabledProviderReceiver() {
		if(registerDisabledProviderReceiver == null) {
			return;
		}
		try {
			context.unregisterReceiver(registerDisabledProviderReceiver);
		} catch (Exception e) {
			//In case is not registered
		}
	}

}