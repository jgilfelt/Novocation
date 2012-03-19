/**
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
 * Code modified by Novoda Ltd, 2011.
 */
package com.novoda.location;

import android.content.Intent;
import android.content.IntentFilter;

public interface Constants {
	
    boolean USE_GPS = true;
    boolean REFRESH_DATA_ON_LOCATION_CHANGED = true;
    boolean ENABLE_PASSIVE_UPDATES = false;
    
    int UPDATES_MAX_DISTANCE = 100;
    long UPDATES_MAX_TIME = 5 * 60 * 1000;
	
    long DEFAULT_INTERVAL_PASSIVE = 15 * 60 * 1000;
	int DEFAULT_DISTANCE_PASSIVE = 300;
    
    String SHARED_PREFERENCE_FILE = "novocation_prefs";
    String SP_KEY_RUN_ONCE = "sp_key_run_once";
    String SP_KEY_PASSIVE_LOCATION_CHANGES = "sp_key_follow_location_changes";
    String SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF = "sp_location_updates_distance_diff";
    String SP_KEY_LOCATION_UPDATES_INTERVAL = "sp_key_location_updates_interval";
    String SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF = "sp_passive_location_updates_distance_diff";
    String SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL = "sp_key_passive_location_updates_interval";
    
    String ACTIVE_LOCATION_UPDATE_ACTION = "com.novoda.location.ACTIVE_LOCATION_UPDATE_ACTION";
    String ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION = "com.novoda.location.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION";
    
    IntentFilter PROVIDER_DISABLED_INTENT_FILTER = new IntentFilter(ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION);
    Intent LOCATION_UPDATE_PROVIDER_DISABLED = new Intent(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION);
}
