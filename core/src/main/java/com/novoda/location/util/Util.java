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
 * 
 * Sections copyright Matthias Kaeppler 2009-2011 based on Ignition-Support.
 */

package com.novoda.location.util;

import com.novoda.location.core.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class Util {

    public static final int API_LEVEL;
    public static final boolean SUPPORTS_GINGERBREAD;
    public static final boolean SUPPORTS_FROYO;

    static {
        int apiLevel = -1;
        try {
            apiLevel = Build.VERSION.class.getField("SDK_INT").getInt(null);
        } catch (Exception e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        }
        API_LEVEL = apiLevel;
        SUPPORTS_GINGERBREAD = API_LEVEL >= 9;
        SUPPORTS_FROYO = API_LEVEL >= 8;
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public static final long getPassiveLocationInterval(Context context) {
        return getSharedPrefs(context).getLong(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL,
                Constants.UPDATES_MAX_TIME);
    }

    public static final int getPassiveLocationDistance(Context context) {
        return getSharedPrefs(context).getInt(Constants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF,
                Constants.UPDATES_MAX_DISTANCE);
    }

}
