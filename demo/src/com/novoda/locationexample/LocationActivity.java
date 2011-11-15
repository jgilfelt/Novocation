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

package com.novoda.locationexample;

import com.novoda.location.R;
import com.novoda.location.core.NovocationLocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class LocationActivity extends Activity {
    
    private NovocationLocation novoLoc;
    
    private TextView lat;
    private TextView lon;
    private TextView dist;
    private TextView time;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialiseViews();
        
        LocationApplication app = (LocationApplication) getApplication();
        novoLoc = app.getLocator();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, filter);
        
        novoLoc.startLocationUpdates(true, true, true);
        
        if (novoLoc.getLocation() != null) {
            updateLocationViews(novoLoc.getLocation());
        }
    }
    
    @Override
    public void onPause() {
        unregisterReceiver(freshLocationReceiver);
        novoLoc.stopLocationUpdates(true);
        super.onPause();
    }
    
    public BroadcastReceiver freshLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocationViews(novoLoc.getLocation());
        }  
    };
    
    private void initialiseViews() {
        lat = (TextView) findViewById(R.id.val_lat);
        lon = (TextView) findViewById(R.id.val_lon);
        dist = (TextView) findViewById(R.id.val_dist);
        time = (TextView) findViewById(R.id.val_time);
    }
    
    private void updateLocationViews(Location loc) {
        lat.setText("" + loc.getLatitude());
        lon.setText("" + loc.getLongitude());
        dist.setText("" + loc.getAccuracy());
        time.setText("" + loc.getTime());
    } 

}
