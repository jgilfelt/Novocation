package com.novoda.location.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public abstract class LocationProviderListener implements LocationListener {

    @Override
    public void onLocationChanged(Location l) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
