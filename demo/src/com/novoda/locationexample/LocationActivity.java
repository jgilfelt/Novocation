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

import java.util.Date;

import com.novoda.locationexample.R;
import com.novoda.location.core.NovocationLocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationActivity extends Activity {
    
    private NovocationLocation novoLoc;
    public ViewGroup viewGroup;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewGroup = (ViewGroup) findViewById(R.id.content);
        
        LocationApplication app = (LocationApplication) getApplication();
        novoLoc = app.getLocator();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, filter);
        novoLoc.startLocationUpdates();
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
            displayNewLocation(novoLoc.getLocation());
        }  
    };
    
    private void displayNewLocation(Location freshLocation) {
        View block = getLayoutInflater().inflate(R.layout.location_table, null);
        
        TextView time = (TextView) block.findViewById(R.id.val_time);
        TextView accuracy = (TextView) block.findViewById(R.id.val_acc);
        TextView provider = (TextView) block.findViewById(R.id.val_prov);
        TextView latitude = (TextView) block.findViewById(R.id.val_lat);
        TextView longitude = (TextView) block.findViewById(R.id.val_lon);
        
        time.setText(getFormattedTime(freshLocation));
        accuracy.setText(freshLocation.getAccuracy() + "m");
        provider.setText(freshLocation.getProvider());
        latitude.setText("" + freshLocation.getLatitude());
        longitude.setText("" + freshLocation.getLongitude());
        
        time.setText(getFormattedTime(freshLocation));
        viewGroup.addView(block);
    }

    private CharSequence getFormattedTime(Location location) {
        Date locDate = new Date(location.getTime());
        CharSequence formattedTime = DateFormat.format("hh:mm:ss", locDate);
        return formattedTime;
    }

}
