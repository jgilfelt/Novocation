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

package com.novoda.location.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NovocationSettings {

    private String packageName;
    private String updateAction;
    
    // Defaults
    private boolean useGps = true;
    private boolean updateOnLocationChange = true;
    private boolean enablePassiveUpdates = false;
    private long updatesInterval = 5 * 60 * 1000; // 5min
    private int updatesDistanceDiff = 100; // 100m
    private long passiveUpdatesInterval = 15 * 60 * 1000; // 15min
    private int passiveUpdatesDistanceDiff = 300; // 300m
    
    public NovocationSettings(String packageName, String updateAction) {
        this.packageName = packageName;
        this.updateAction = updateAction;
    }
    
    public String getPackageName() {
        return packageName;
    }

    public String getUpdateAction() {
        return updateAction;
    }
    
    public boolean shouldUseGps() {
        return useGps;
    }

    public void setUseGps(boolean useGps) {
        this.useGps = useGps;
    }

    public boolean shouldUpdateLocation() {
        return updateOnLocationChange;
    }

    public void setUpdateOnLocationChange(boolean updateOnLocationChange) {
        this.updateOnLocationChange = updateOnLocationChange;
    }

    public boolean shouldEnablePassiveUpdates() {
        return enablePassiveUpdates;
    }

    public void setEnablePassiveUpdates(boolean enablePassiveUpdates) {
        this.enablePassiveUpdates = enablePassiveUpdates;
    }

    public long getUpdatesInterval() {
        return updatesInterval;
    }

    public void setUpdatesInterval(long updatesInterval) {
        this.updatesInterval = updatesInterval;
    }

    public int getUpdatesDistanceDiff() {
        return updatesDistanceDiff;
    }

    public void setUpdatesDistanceDiff(int updatesDistanceDiff) {
        this.updatesDistanceDiff = updatesDistanceDiff;
    }

    public long getPassiveUpdatesInterval() {
        return passiveUpdatesInterval;
    }

    public void setPassiveUpdatesInterval(long passiveUpdatesInterval) {
        this.passiveUpdatesInterval = passiveUpdatesInterval;
    }

    public int getPassiveUpdatesDistanceDiff() {
        return passiveUpdatesDistanceDiff;
    }

    public void setPassiveUpdatesDistanceDiff(int passiveUpdatesDistanceDiff) {
        this.passiveUpdatesDistanceDiff = passiveUpdatesDistanceDiff;
    }
    
    public void saveCurrentSettingsToPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(Constants.SP_KEY_FOLLOW_LOCATION_CHANGES, updateOnLocationChange);
        editor.putInt(Constants.SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF, updatesDistanceDiff);
        editor.putLong(Constants.SP_KEY_LOCATION_UPDATES_INTERVAL, updatesInterval);
        editor.putInt(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF, passiveUpdatesDistanceDiff);
        editor.putLong(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL, passiveUpdatesInterval);
        editor.putBoolean(Constants.SP_KEY_RUN_ONCE, true);
        editor.commit();
    }

}
