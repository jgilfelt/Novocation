package com.novoda.location;

import com.novoda.location.exception.NoProviderAvailable;

import android.content.Context;
import android.location.Location;

public interface Locator {

	void prepare(Context c, Settings settings);

	Location getLocation();

	void setLocation(Location location);

	Settings getSettings();

	void startLocationUpdates() throws NoProviderAvailable;

	void stopLocationUpdates();

	boolean isNetworkProviderEnabled();

	boolean isGpsProviderEnabled();

}
