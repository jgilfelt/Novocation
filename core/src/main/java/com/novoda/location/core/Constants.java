/*
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
 * Code modified by Novoda, 2011.
 */

package com.novoda.location.core;

public class Constants {

    public static final boolean USE_GPS = true;
    public static final boolean REFRESH_DATA_ON_LOCATION_CHANGED = true;
    
    // Maximum distance(metres)/time(ms) between location updates.
    public static final int UPDATES_MAX_DISTANCE = 100;
    public static final long UPDATES_MAX_TIME = 5 * 60 * 1000;
    
    public static final String SHARED_PREFERENCE_FILE = "novocation_prefs";
    public static final String SP_KEY_RUN_ONCE = "sp_key_run_once";
    public static String SP_KEY_PASSIVE_LOCATION_CHANGES = "sp_key_follow_location_changes";
    public static String SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF = "sp_location_updates_distance_diff";
    public static String SP_KEY_LOCATION_UPDATES_INTERVAL = "sp_key_location_updates_interval";
    public static String SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF = "sp_passive_location_updates_distance_diff";
    public static String SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL = "sp_key_passive_location_updates_interval";
    
    public static final String ACTIVE_LOCATION_UPDATE_ACTION = "com.novoda.location.ACTIVE_LOCATION_UPDATE_ACTION";
    public static final String ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION = "com.novoda.location.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION";
    
}
