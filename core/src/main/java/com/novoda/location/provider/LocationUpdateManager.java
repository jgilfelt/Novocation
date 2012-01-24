package com.novoda.location.provider;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.novoda.location.Constants;
import com.novoda.location.LocatorSettings;
import com.novoda.location.exception.NoProviderAvailable;
import com.novoda.location.provider.task.LastKnownLocationTask;
import com.novoda.location.receiver.PassiveLocationChanged;
import com.novoda.location.util.ApiLevelDetector;

public class LocationUpdateManager {

    private final LocationProviderFactory locationProviderFactory = new LocationProviderFactory();
    private final LocatorSettings settings;
    private final Criteria criteria;
    private final LocationManager locationManager;
    private final LocationUpdateRequester locationUpdateRequester;
    private PendingIntent locationListenerPendingIntent;
    private PendingIntent locationListenerPassivePendingIntent;
    private AsyncTask<Void, Void, Location> lastKnownLocationTask;
    
    public LocationUpdateManager(LocatorSettings settings, Criteria criteria, Context context, LocationManager locationManager) {
    	this.settings = settings;
    	this.criteria = criteria;
    	this.locationManager = locationManager;
    	locationUpdateRequester = locationProviderFactory.getLocationUpdateRequester(locationManager);
    	configurePendingIntents(context);
    }
    
    private void configurePendingIntents(Context context) {
        Intent activeIntent = new Intent(Constants.ACTIVE_LOCATION_UPDATE_ACTION);
        activeIntent.setPackage(settings.getPackageName());
        locationListenerPendingIntent = PendingIntent.getBroadcast(context, 0, activeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent passiveIntent = new Intent(context, PassiveLocationChanged.class);
        passiveIntent.setPackage(settings.getPackageName());
        locationListenerPassivePendingIntent = PendingIntent.getBroadcast(context, 0, passiveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
	public void requestActiveLocationUpdates() throws NoProviderAvailable {
		try { 
			locationUpdateRequester.requestActiveLocationUpdates(settings.getUpdatesInterval(),
			                                                     settings.getUpdatesDistance(), 
			                                                     criteria, 
			                                                     locationListenerPendingIntent);
		} catch(IllegalArgumentException iae) {
			throw new NoProviderAvailable();
		}
	}

	public void requestPassiveLocationUpdates() {
		if (ApiLevelDetector.supportsFroyo() && settings.shouldEnablePassiveUpdates()) {
			locationUpdateRequester.requestPassiveLocationUpdates(settings, locationListenerPassivePendingIntent);
		}
	}
	
	public void removeUpdates() {
		locationManager.removeUpdates(locationListenerPendingIntent);
	}
	
	public void removePassiveUpdates() {
		locationManager.removeUpdates(locationListenerPassivePendingIntent);
	}
    
    public void fetchLastKnownLocation(Context context) {
    	LastLocationFinder finder = locationProviderFactory.getLastLocationFinder(locationManager, context);
        lastKnownLocationTask = new LastKnownLocationTask(finder, settings);
        lastKnownLocationTask.execute();
    }
    
	public void stopFetchLastKnownLocation() {
		if (lastKnownLocationTask == null) {
			return;
        }
		lastKnownLocationTask.cancel(true);
	}

}
