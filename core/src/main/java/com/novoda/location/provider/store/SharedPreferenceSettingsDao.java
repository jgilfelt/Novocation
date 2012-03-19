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
 * 
 * Sections copyright Matthias Kaeppler 2009-2011 based on Ignition-Support.
 */
package com.novoda.location.provider.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.novoda.location.Constants;
import com.novoda.location.LocatorSettings;

public class SharedPreferenceSettingsDao implements SettingsDao {

	@Override
    public long getPassiveLocationInterval(Context context) {
        return getSharedPrefs(context).getLong(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL,
                Constants.UPDATES_MAX_TIME);
    }

    @Override
    public int getPassiveLocationDistance(Context context) {
        return getSharedPrefs(context).getInt(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF,
                Constants.UPDATES_MAX_DISTANCE);
    }

    @Override
    public void persistSettingsToPreferences(Context context, LocatorSettings settings) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(Constants.SP_KEY_PASSIVE_LOCATION_CHANGES, settings.shouldEnablePassiveUpdates());
        editor.putInt(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF, settings.getPassiveUpdatesDistance());
        editor.putLong(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL, settings.getPassiveUpdatesInterval());
        editor.putBoolean(Constants.SP_KEY_RUN_ONCE, true);
        editor.commit();
    }
    
    @Override
	public boolean isPassiveLocationChanges(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean(Constants.SP_KEY_PASSIVE_LOCATION_CHANGES, true);
	}

    @Override
	public boolean isRunOnce(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean(Constants.SP_KEY_RUN_ONCE, false);
	}

    
    private SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }
    
}
