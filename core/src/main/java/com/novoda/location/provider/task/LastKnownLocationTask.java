package com.novoda.location.provider.task;

import android.location.Location;
import android.os.AsyncTask;

import com.novoda.location.LocatorFactory;
import com.novoda.location.LocatorSettings;
import com.novoda.location.provider.LastLocationFinder;

public class LastKnownLocationTask extends AsyncTask<Void, Void, Location> {
    
    private LastLocationFinder lastLocationFinder;
    private int locationUpdateDistanceDiff;
    private long locationUpdateInterval;

    public LastKnownLocationTask(LastLocationFinder lastLocationFinder, LocatorSettings settings) {
        this.lastLocationFinder = lastLocationFinder;
        this.locationUpdateDistanceDiff = settings.getUpdatesDistance();
        this.locationUpdateInterval = settings.getUpdatesInterval();
    }

    @Override
    protected Location doInBackground(Void... params) {
        long minimumTime = System.currentTimeMillis() - locationUpdateInterval;
        return lastLocationFinder.getLastBestLocation(locationUpdateDistanceDiff, minimumTime);
    }

    @Override
    protected void onPostExecute(Location lastKnownLocation) {
        if (lastKnownLocation == null) {
        	return;
        }
        LocatorFactory.setLocation(lastKnownLocation);
    }

    @Override
    protected void onCancelled() {
        lastLocationFinder.cancel();
    }
}
