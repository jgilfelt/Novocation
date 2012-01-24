package com.novoda.location.util;

import android.location.Location;

import com.novoda.location.LocatorSettings;

/*
 * This code is copied from
 * TODO add some sort of google license
 * 
 * http://developer.android.com/guide/topics/location/obtaining-user-location.html
 */
public class LocationAccuracy {
	
	private final LocatorSettings settings;

    public LocationAccuracy(LocatorSettings settings) {
	    this.settings = settings;
    }

    public boolean isWorseLocation(Location location, Location currentBestLocation) {
		return !isBetterLocation(location, currentBestLocation);
	}
	
	public boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        return true;
	    }
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    long updatesInterval = settings.getUpdatesInterval();
        boolean isSignificantlyNewer = timeDelta > updatesInterval;
	    boolean isSignificantlyOlder = timeDelta < -updatesInterval;
	    boolean isNewer = timeDelta > 0;
	    if (isSignificantlyNewer) {
	        return true;
	    } else if (isSignificantlyOlder) {
	        return false;
	    }
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
}
