package com.novoda.location.provider.store;

import com.novoda.location.Settings;

import android.content.Context;

public interface SettingsDao {

	long getPassiveLocationInterval(Context context);

	int getPassiveLocationDistance(Context context);

	void persistSettingsToPreferences(Context context, Settings settings);

	boolean isRunOnce(Context context);

	boolean isPassiveLocationChanges(Context context);

}
