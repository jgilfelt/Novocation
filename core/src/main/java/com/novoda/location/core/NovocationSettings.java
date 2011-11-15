package com.novoda.location.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NovocationSettings {

    private String packageName;
    private String updateAction;
    
    // Defaults
    private boolean useGps = true;
    private boolean refreshOnLocationChange = true;
    private boolean enablePassiveUpdates = false;
    private long locationUpdatesInterval = 5 * 60 * 1000; // 5min
    private int locationUpdatesDistanceDiff = 100; // 100m
    private long passiveLocationUpdatesInterval = 15 * 60 * 1000; // 15min
    private int passiveLocatoionUpdatesDistanceDiff = 300; // 300m
    
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
        return refreshOnLocationChange;
    }

    public void setRefreshOnLocationChange(boolean refreshOnLocationChange) {
        this.refreshOnLocationChange = refreshOnLocationChange;
    }

    public boolean shouldEnablePassiveUpdates() {
        return enablePassiveUpdates;
    }

    public void setEnablePassiveUpdates(boolean enablePassiveUpdates) {
        this.enablePassiveUpdates = enablePassiveUpdates;
    }

    public long getLocationUpdatesInterval() {
        return locationUpdatesInterval;
    }

    public void setLocationUpdatesInterval(long locationUpdatesInterval) {
        this.locationUpdatesInterval = locationUpdatesInterval;
    }

    public int getLocationUpdatesDistanceDiff() {
        return locationUpdatesDistanceDiff;
    }

    public void setLocationUpdatesDistanceDiff(int locationUpdatesDistanceDiff) {
        this.locationUpdatesDistanceDiff = locationUpdatesDistanceDiff;
    }

    public long getPassiveLocationUpdatesInterval() {
        return passiveLocationUpdatesInterval;
    }

    public void setPassiveLocationUpdatesInterval(long passiveLocationUpdatesInterval) {
        this.passiveLocationUpdatesInterval = passiveLocationUpdatesInterval;
    }

    public int getPassiveLocatoionUpdatesDistanceDiff() {
        return passiveLocatoionUpdatesDistanceDiff;
    }

    public void setPassiveLocatoionUpdatesDistanceDiff(int passiveLocatoionUpdatesDistanceDiff) {
        this.passiveLocatoionUpdatesDistanceDiff = passiveLocatoionUpdatesDistanceDiff;
    }
    
    public void saveCurrentSettingsToPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(Constants.SP_KEY_FOLLOW_LOCATION_CHANGES, refreshOnLocationChange);
        editor.putInt(Constants.SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF, locationUpdatesDistanceDiff);
        editor.putLong(Constants.SP_KEY_LOCATION_UPDATES_INTERVAL, locationUpdatesInterval);
        editor.putInt(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF, passiveLocatoionUpdatesDistanceDiff);
        editor.putLong(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL, passiveLocationUpdatesInterval);
        editor.putBoolean(Constants.SP_KEY_RUN_ONCE, true);
        editor.commit();
    }

}
