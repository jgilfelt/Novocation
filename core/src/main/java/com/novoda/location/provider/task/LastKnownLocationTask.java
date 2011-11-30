package com.novoda.location.provider.task;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.novoda.location.LocationFinder;
import com.novoda.location.LocationSettings;
import com.novoda.location.provider.LastLocationFinder;

public class LastKnownLocationTask extends AsyncTask<Void, Void, Location> {
    
    private Context context;
    private LastLocationFinder lastLocationFinder;
    private int locationUpdateDistanceDiff;
    private long locationUpdateInterval;

    public LastKnownLocationTask(LastLocationFinder lastLocationFinder, Context context, LocationSettings settings) {
        this.context = context;
        this.lastLocationFinder = lastLocationFinder;
        this.locationUpdateDistanceDiff = settings.getUpdatesDistance();
        this.locationUpdateInterval = settings.getUpdatesInterval();
    }

    @Override
    protected Location doInBackground(Void... params) {
        return getLastKnownLocation(context);
    }

    @Override
    protected void onPostExecute(Location lastKnownLocation) {
        if (lastKnownLocation == null) {
        	return;
        }
        LocationFinder.getInstance().setLocation(lastKnownLocation);
    }

    protected Location getLastKnownLocation(Context context) {
        return lastLocationFinder.getLastBestLocation(locationUpdateDistanceDiff, System.currentTimeMillis()
                - locationUpdateInterval);
    }

    @Override
    protected void onCancelled() {
        lastLocationFinder.cancel();
    }
}
