/*
 * Copyright 2011 Google Inc.
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
 * 
 * Code modified by Novoda, 2011.
 */

package com.novoda.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.novoda.location.Constants;
import com.novoda.location.LocatorFactory;
import com.novoda.location.util.Log;

public class LocationChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
    	Log.v("LocationChanged onReceive");
    	if(i == null) {
    		return;
    	}
        if (isProviderNotEnabled(i)) {
        	Log.v("LocationChanged provider not enabled");
            context.sendBroadcast(Constants.LOCATION_UPDATE_PROVIDER_DISABLED);
        }
        if (hasLocationChanged(i)) {
        	Log.v("LocationChanged has location changes");
            LocatorFactory.setLocation(getLocation(i));
        }
    }
    
    private boolean isProviderNotEnabled(Intent intent) {
    	if (!intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
    		return false;
    	}
    	return !intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, true);
    }
    
    private boolean hasLocationChanged(Intent intent) {
    	return intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED);
    }
    
    private Location getLocation(Intent intent) {
    	return (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
    }
}