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

package com.novoda.location.core;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.novoda.location.location.LocationUpdateRequester;
import com.novoda.location.location.PlatformSpecificImplementationFactory;
import com.novoda.location.receiver.PassiveLocationChangedReceiver;
import com.novoda.location.task.LastKnownLocationTask;
import com.novoda.location.util.Util;

public class LocationFinder {

    private Context appContext;
    private LocationSettings settings;
    private volatile Location currentLocation;
    private AsyncTask<Void, Void, Location> lastKnownLocationTask;

    private LocationManager locationManager;
    private Criteria criteria;
    private LocationUpdateRequester locationUpdateRequester;
    private PendingIntent locationListenerPendingIntent;
    private PendingIntent locationListenerPassivePendingIntent;
    private BetterProviderListener bestInactiveLocationProviderListener;

    private LocationFinder() {
        // Singleton.
    };

    private static class NovocationLocationHolder {
        public static final LocationFinder instance = new LocationFinder();
    }

    public static LocationFinder getInstance() {
        return NovocationLocationHolder.instance;
    }

    public void connect(Context appContext, LocationSettings settings) {
        this.appContext = appContext;
        this.settings = settings;
        locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLocation() {
        return currentLocation;
    }

    public void setLocation(Location location) {
        currentLocation = location;
        sendLocationUpdateBroadcast();
    }

    private void sendLocationUpdateBroadcast() {
        if (appContext != null) {
            Intent broadcast = new Intent();
            broadcast.setAction(settings.getUpdateAction());
            broadcast.setPackage(settings.getPackageName());
            appContext.sendBroadcast(broadcast);
        }
    }

    public LocationSettings getSettings() {
        return settings;
    }

    public void startLocationUpdates() {

        createActiveUpdateCriteria();

        // Setup the location update Pending Intents
        Intent activeIntent = new Intent(Constants.ACTIVE_LOCATION_UPDATE_ACTION);
        locationListenerPendingIntent = PendingIntent.getBroadcast(appContext, 0, activeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent passiveIntent = new Intent(appContext, PassiveLocationChangedReceiver.class);
        locationListenerPassivePendingIntent = PendingIntent.getBroadcast(appContext, 0, passiveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Instantiate a Location Update Requester class based on the available
        // platform version. This will be used to request location updates.
        locationUpdateRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(locationManager);
        settings.savePassiveSettingsToPreferences(appContext);

        if (currentLocation != null) {
            sendLocationUpdateBroadcast();
        } else {
            // Get the last known location. This isn't directly affecting the
            // UI, so put it on a worker thread.
            lastKnownLocationTask = new LastKnownLocationTask(appContext, settings.getUpdatesDistance(),
                    settings.getUpdatesInterval());
            lastKnownLocationTask.execute();
        }

        // If we have requested location updates, turn them on here.
        if (settings.shouldUpdateLocation()) {
            requestLocationUpdates(appContext);
        }
    }

    private void createActiveUpdateCriteria() {
        criteria = new Criteria();
        if (settings.shouldUseGps()) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        } else {
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }
    }

    public void stopLocationUpdates(boolean finishing) {
        if (settings.shouldUpdateLocation()) {
            disableLocationUpdates(settings.shouldEnablePassiveUpdates(), finishing);
        }
    }

    /**
     * If the Location Provider we're using to receive location updates is
     * disabled while the app is running, this Receiver will be notified,
     * allowing us to re-register our Location Receivers using the best
     * available Location Provider is still available.
     */
    protected BroadcastReceiver locProviderDisabledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean providerDisabled = !intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            // Re-register the location listeners using the best available
            // Location Provider.
            if (providerDisabled) {
                requestLocationUpdates(context);
            }
        }
    };

    /**
     * Start listening for location updates.
     */
    protected void requestLocationUpdates(Context context) {

        // Normal updates while activity is visible.
        locationUpdateRequester.requestLocationUpdates(settings.getUpdatesInterval(),
                settings.getUpdatesDistance(), criteria, locationListenerPendingIntent);

        // Register a receiver that listens for when the provider I'm using has
        // been disabled.
        IntentFilter intentFilter = new IntentFilter(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION);
        context.registerReceiver(locProviderDisabledReceiver, intentFilter);

        // Register a receiver that listens for when a better provider than I'm
        // using becomes available.
        String bestProvider = locationManager.getBestProvider(criteria, false);
        String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
            bestInactiveLocationProviderListener = new BetterProviderListener(context);
            locationManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener,
                    context.getMainLooper());
        }
        locationManager.removeUpdates(locationListenerPassivePendingIntent);
    }

    /**
     * Stop listening for location updates
     */
    protected void disableLocationUpdates(boolean enablePassiveLocationUpdates, boolean finishing) {

        appContext.unregisterReceiver(locProviderDisabledReceiver);
        locationManager.removeUpdates(locationListenerPendingIntent);
        if (bestInactiveLocationProviderListener != null) {
            locationManager.removeUpdates(bestInactiveLocationProviderListener);
        }

        if (finishing && lastKnownLocationTask != null) {
            lastKnownLocationTask.cancel(true);
        }

        if (Util.SUPPORTS_FROYO && enablePassiveLocationUpdates) {
            // Passive location updates from 3rd party apps when the Activity
            // isn't visible. Only for Android 2.2+.
            locationUpdateRequester.requestPassiveLocationUpdates(settings.getUpdatesInterval(),
                    settings.getUpdatesDistance(), locationListenerPassivePendingIntent);
        }
    }

    /**
     * If the best Location Provider (usually GPS) is not available when we
     * request location updates, this listener will be notified if / when it
     * becomes available. It calls requestLocationUpdates to re-register the
     * location listeners using the better Location Provider.
     */
    private class BetterProviderListener implements LocationListener {
        private Context context;

        public BetterProviderListener(Context appContext) {
            this.context = appContext;
        }

        @Override
        public void onLocationChanged(Location l) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Re-register the location listeners using the better Location
            // Provider.
            requestLocationUpdates(context);
        }

    }

}
