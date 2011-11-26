package com.novoda.location.task;

import com.novoda.location.core.LocationFinder;
import com.novoda.location.location.ILastLocationFinder;
import com.novoda.location.location.PlatformSpecificImplementationFactory;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

public class LastKnownLocationTask extends AsyncTask<Void, Void, Location> {
    
    private Context context;
    private ILastLocationFinder lastLocationFinder;
    private int locationUpdateDistanceDiff;
    private long locationUpdateInterval;

    /**
     * @param appContext
     * @param locationUpdateDistanceDiff
     * @param locationUpdateInterval
     */
    public LastKnownLocationTask(Context appContext, int locationUpdateDistanceDiff, long locationUpdateInterval) {
        this.context = appContext;
        // Instantiate a LastLocationFinder class. This will be used to find the
        // last known location when the application starts.
        lastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(context);
        this.locationUpdateDistanceDiff = locationUpdateDistanceDiff;
        this.locationUpdateInterval = locationUpdateInterval;
    }

    @Override
    protected Location doInBackground(Void... params) {
        return getLastKnownLocation(context);
    }

    @Override
    protected void onPostExecute(Location lastKnownLocation) {
        if (lastKnownLocation != null) {
            LocationFinder.getInstance().setLocation(lastKnownLocation);
        }
    }

    /**
     * Find the last known location (using a {@link LastLocationFinder}) and
     * updates the place list accordingly.
     */
    protected Location getLastKnownLocation(Context context) {
        // Find the last known location, specifying a required accuracy of
        // within the min distance between updates and a required latency of the
        // minimum time required between updates.
        return lastLocationFinder.getLastBestLocation(locationUpdateDistanceDiff, System.currentTimeMillis()
                - locationUpdateInterval);
    }

    @Override
    protected void onCancelled() {
        lastLocationFinder.cancel();
    }
}
