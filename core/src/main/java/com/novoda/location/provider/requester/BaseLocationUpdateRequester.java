/**
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
package com.novoda.location.provider.requester;

import com.novoda.location.LocatorSettings;
import com.novoda.location.provider.LocationProviderFactory;
import com.novoda.location.provider.LocationUpdateRequester;
import com.novoda.location.provider.store.SettingsDao;

import android.app.PendingIntent;
import android.content.Context;
import android.location.LocationManager;

public abstract class BaseLocationUpdateRequester implements LocationUpdateRequester {

	protected LocationManager locationManager;

	protected BaseLocationUpdateRequester(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	@Override
	public void requestPassiveLocationUpdates(Context context, PendingIntent pendingIntent) {
		SettingsDao settingsDao = new LocationProviderFactory().getSettingsDao();
        long passiveLocationTime = settingsDao.getPassiveLocationInterval(context);
        int passiveLocationDistance = settingsDao.getPassiveLocationDistance(context);
        requestPassiveLocationUpdates(passiveLocationTime, passiveLocationDistance, pendingIntent);
	}
	
	@Override
	public void requestPassiveLocationUpdates(LocatorSettings settings, PendingIntent pendingIntent) {
		requestPassiveLocationUpdates(settings.getPassiveUpdatesInterval(), settings.getPassiveUpdatesDistance(), pendingIntent);
	}
	
	protected abstract void requestPassiveLocationUpdates(long minTime, long minDistance, PendingIntent pendingIntent);

	@Override
	public void removeLocationUpdates(PendingIntent pendingIntent) {
		try {
			locationManager.removeUpdates(pendingIntent);
		} catch (IllegalArgumentException e) {
			
		}
	}
}
