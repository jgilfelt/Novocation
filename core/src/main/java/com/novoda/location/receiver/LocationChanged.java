/**
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
 * Code modified by Novoda Ltd, 2011.
 */
package com.novoda.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.novoda.location.Constants;
import com.novoda.location.LocatorFactory;

public class LocationChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
    	if(i == null) {
    		return;
    	}
    	if(providerStatusHasChanged(i)){
    	    broadcastProviderStatusHasChanged(context, i);
    	}
    	
        if (locationHasChanged(i)) {
            LocatorFactory.setLocation(getLocation(i));
        }
    }
    
    private boolean providerStatusHasChanged(Intent i) {
        return i.hasExtra(LocationManager.KEY_PROVIDER_ENABLED);
    }

    private void broadcastProviderStatusHasChanged(Context context, Intent i) {
        Intent providerStatusChanged;
        if(providerHasBeenEnabled(i)){
            providerStatusChanged = new Intent(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_ENABLED_ACTION);
        }else{
            providerStatusChanged = new Intent(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION);    
        }
        context.sendBroadcast(providerStatusChanged);
    }

    private boolean providerHasBeenEnabled(Intent i) {
        return i.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
    }
    
    private boolean locationHasChanged(Intent intent) {
    	return intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED);
    }
    
    private Location getLocation(Intent intent) {
    	return (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
    }
}