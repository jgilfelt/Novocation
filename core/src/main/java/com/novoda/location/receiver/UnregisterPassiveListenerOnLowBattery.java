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
 */
package com.novoda.location.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class UnregisterPassiveListenerOnLowBattery extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        if(isBatteryLow(intent)) {
        	state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        changeStateToComponent(context, state);
    }
    
	private boolean isBatteryLow(Intent intent) {
		if(intent == null) {
			return false;
		}
		return Intent.ACTION_BATTERY_LOW.equals(intent.getAction());
	}

	private void changeStateToComponent(Context c, int state) {
		PackageManager pm = c.getPackageManager();
		ComponentName cr = new ComponentName(c, PassiveLocationChanged.class);
        pm.setComponentEnabledSetting(cr, state, PackageManager.DONT_KILL_APP);
	}
}