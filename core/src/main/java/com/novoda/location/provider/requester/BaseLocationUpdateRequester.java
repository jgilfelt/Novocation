package com.novoda.location.provider.requester;

import com.novoda.location.LocationSettings;
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
	public void requestPassiveLocationUpdates(LocationSettings settings, PendingIntent pendingIntent) {
		requestPassiveLocationUpdates(settings.getPassiveUpdatesInterval(), settings.getPassiveUpdatesDistance(), pendingIntent);
	}
	
	protected abstract void requestPassiveLocationUpdates(long minTime, long minDistance, PendingIntent pendingIntent);
}
