package com.novoda.location.provider.store;

import com.novoda.location.LocationSettings;

import android.content.Context;

public interface SettingsDao {

	long getPassiveLocationInterval(Context context);

	int getPassiveLocationDistance(Context context);

	void persistSettingsToPreferences(Context context, LocationSettings settings);

	boolean isRunOnce(Context context);

	boolean isPassiveLocationChanges(Context context);

}
