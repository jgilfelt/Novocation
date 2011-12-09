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
 */

package com.novoda.locationdemo;

import roboguice.application.RoboApplication;

import com.bugsense.trace.BugSenseHandler;
import com.novoda.location.LocatorFactory;
import com.novoda.location.Settings;
import com.novoda.location.Locator;

public class LocationDemo extends RoboApplication {
    
    public static final String PACKAGE_NAME = "com.novoda.locationdemo";
    public static final String LOCATION_UPDATE_ACTION = "com.novoda.locationdemo.action.ACTION_FRESH_LOCATION";

    //==================================================
    // TODO
    private static Locator locator;
    //==================================================
    
    @Override
    public void onCreate() {
        super.onCreate();
        //==================================================
        // TODO
        // Connect the location finder with relevant settings.
        Settings settings = new Settings(PACKAGE_NAME, LOCATION_UPDATE_ACTION);
        settings.setUpdatesInterval(3 * 60 * 1000);
        settings.setUpdatesDistance(50);
        locator = LocatorFactory.getInstance();
        locator.prepare(getApplicationContext(), settings);
        //==================================================
        
        BugSenseHandler.setup(this, "e3cf60f2");
    }

    //==================================================
    // TODO
    public Locator getLocator() {
        return locator;
    }
    //==================================================

}
