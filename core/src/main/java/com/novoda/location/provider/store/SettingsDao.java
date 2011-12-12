package com.novoda.location.provider.store;

import com.novoda.location.LocatorSettings;

import android.content.Context;

public interface SettingsDao {

	long getPassiveLocationInterval(Context context);

	int getPassiveLocationDistance(Context context);

	void persistSettingsToPreferences(Context context, LocatorSettings settings);

	boolean isRunOnce(Context context);

	boolean isPassiveLocationChanges(Context context);

}
