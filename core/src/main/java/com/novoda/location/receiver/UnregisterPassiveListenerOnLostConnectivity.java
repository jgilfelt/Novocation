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
package com.novoda.location.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UnregisterPassiveListenerOnLostConnectivity extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context c, Intent intent) {
        if (isNotConnected(c)) {
        	return;
        }
        changeStateToComponent(c, UnregisterPassiveListenerOnLostConnectivity.class);
        changeStateToComponent(c, LocationChanged.class);
        changeStateToComponent(c, PassiveLocationChanged.class);
    }

	private void changeStateToComponent(Context context, Class<? extends BroadcastReceiver> clazz) {
		PackageManager pm = context.getPackageManager();
		ComponentName cr = new ComponentName(context, clazz);
        pm.setComponentEnabledSetting(cr, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);
	}

	private boolean isNotConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork == null) {
			return true;
		}
		if(activeNetwork.isConnectedOrConnecting()) {
			return false;
		}
		return true;
	}
}