package com.novoda.location;

import com.novoda.location.exception.NoProviderAvailable;

import android.content.Context;
import android.location.Location;

public interface Locator {

	void prepare(Context c, LocatorSettings settings);

	Location getLocation();

	void setLocation(Location location);

	LocatorSettings getSettings();

	void startLocationUpdates() throws NoProviderAvailable;

	void stopLocationUpdates();

	boolean isNetworkProviderEnabled();

	boolean isGpsProviderEnabled();

}
