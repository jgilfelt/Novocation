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
package com.novoda.location;

public class LocatorSettings {

    private boolean useGps = Constants.USE_GPS;
    private boolean updateOnLocationChange = Constants.REFRESH_DATA_ON_LOCATION_CHANGED;
    private long updatesInterval = Constants.UPDATES_MAX_TIME;
    private int updatesDistance = Constants.UPDATES_MAX_DISTANCE;
    private long passiveUpdatesInterval = Constants.DEFAULT_INTERVAL_PASSIVE;
    private int passiveUpdatesDistance = Constants.DEFAULT_DISTANCE_PASSIVE;
    private boolean enablePassiveUpdates = Constants.ENABLE_PASSIVE_UPDATES;
    
    private String packageName;
    private String updateAction;
    
    public LocatorSettings(String packageName, String updateAction) {
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

    public int getUpdatesDistance() {
        return updatesDistance;
    }

    public void setUpdatesDistance(int updatesDistance) {
        this.updatesDistance = updatesDistance;
    }

    public long getPassiveUpdatesInterval() {
        return passiveUpdatesInterval;
    }

    public void setPassiveUpdatesInterval(long passiveUpdatesInterval) {
        this.passiveUpdatesInterval = passiveUpdatesInterval;
    }

    public int getPassiveUpdatesDistance() {
        return passiveUpdatesDistance;
    }

    public void setPassiveUpdatesDistance(int passiveUpdatesDistance) {
        this.passiveUpdatesDistance = passiveUpdatesDistance;
    }

}
