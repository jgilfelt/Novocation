package com.novoda.location.demo.simple;

import android.app.Application;

import com.novoda.location.Locator;
import com.novoda.location.LocatorFactory;
import com.novoda.location.LocatorSettings;

public class ExampleApplication extends Application {
    
    public static final String PACKAGE_NAME = "com.novoda.location.demo.simple";
    public static final String LOCATION_UPDATE_ACTION = "com.novoda.location.demo.simple.action.ACTION_FRESH_LOCATION";

    private static Locator locator;
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Connect the location finder with relevant settings.
        LocatorSettings settings = new LocatorSettings(PACKAGE_NAME, LOCATION_UPDATE_ACTION);
        settings.setUpdatesInterval(3 * 60 * 1000);
        settings.setUpdatesDistance(50);
        locator = LocatorFactory.getInstance();
        locator.prepare(getApplicationContext(), settings);
    }

    public Locator getLocator() {
        return locator;
    }

}
