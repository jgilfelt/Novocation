package com.novoda.location;

import com.novoda.location.locator.DefaultLocator;

import android.location.Location;

public class LocatorFactory {
	
	private static Locator instance; 
	
	public static Locator getInstance() {
		if(instance == null) {
			instance = new DefaultLocator();
		}
		return instance;
	}

	public static void setLocation(Location location) {
		getInstance().setLocation(location);
	}

	public static Location getLocation() {
		return getInstance().getLocation();
	}

}
