package com.novoda.location.demo.simple;

import java.util.Date;

import com.novoda.location.Locator;
import com.novoda.location.exception.NoProviderAvailable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This activity registers a broadcast receiver so that it can do
 * something on each location update. You could also simply call
 * locator.getLocation() some time after the onCreate method.
 */
public class ExampleActivity extends Activity {

    private Locator locator;
    
    TextView time;
    TextView accuracy;
    TextView provider;
    TextView latitude;
    TextView longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        time = (TextView) findViewById(R.id.val_time);
        accuracy = (TextView) findViewById(R.id.val_acc);
        provider = (TextView) findViewById(R.id.val_prov);
        latitude = (TextView) findViewById(R.id.val_lat);
        longitude = (TextView) findViewById(R.id.val_lon);

        // Get the reference to the locator object.
        ExampleApplication app = (ExampleApplication) getApplication();
        locator = app.getLocator();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register broadcast receiver and start location updates.
        IntentFilter f = new IntentFilter();
        f.addAction(ExampleApplication.LOCATION_UPDATE_ACTION);
        registerReceiver(freshLocationReceiver, f);

        try {
            locator.startLocationUpdates();
        } catch (NoProviderAvailable npa) {
            Toast.makeText(this, "No provider available", Toast.LENGTH_LONG).show();
        }
    }

    // Receiver to do something in response to fresh location update.
    public BroadcastReceiver freshLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doSomethingWithFreshLocation(locator.getLocation());
        }
    };

    private void doSomethingWithFreshLocation(Location loc) {
        Log.e("tst", "update");
        time.setText(DateFormat.format("hh:mm:ss", new Date(loc.getTime())));
        accuracy.setText(loc.getAccuracy() + "m");
        provider.setText(loc.getProvider());
        latitude.setText("" + loc.getLatitude());
        longitude.setText("" + loc.getLongitude());
    }

    @Override
    public void onPause() {
        // Unregister broadcast receiver and stop location updates.
        unregisterReceiver(freshLocationReceiver);
        locator.stopLocationUpdates();

        super.onPause();
    }
}