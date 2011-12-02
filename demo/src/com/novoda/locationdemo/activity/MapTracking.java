package com.novoda.locationdemo.activity;

import java.util.List;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.novoda.locationdemo.R;
import com.novoda.locationdemo.activity.location.LocationItemizedOverlay;

public class MapTracking extends RoboMapActivity {

	private static final String LAT_EXTRA = "com.novoda.locationdemo.activity.LAT_EXTRA";
	private static final String LON_EXTRA = "com.novoda.locationdemo.activity.LON_EXTRA";
	private static final String ACCURACY_EXTRA = "com.novoda.locationdemo.activity.ACCURACY_EXTRA";
	
	public static final Intent getIntent(Context context, Location location) {
		Intent i = new Intent(context, MapTracking.class);
		int lat = (int)(location.getLatitude() * 1E6); 
		int lon = (int)(location.getLongitude() * 1E6);
		i.putExtra(LAT_EXTRA, lat);
		i.putExtra(LON_EXTRA, lon);
		i.putExtra(ACCURACY_EXTRA, location.getAccuracy());
		return i;
	}
	
	@InjectView(R.id.mapView) MapView mapView;
	
	private static Drawable icon;
	private List<Overlay> mapOverlays;
	private LocationItemizedOverlay point;
	private MapController mapController;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_tracking_activity);        
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(17);
        mapOverlays = mapView.getOverlays();
        update();
    }
    
    private void update() {
        mapOverlays.clear();
        point = new LocationItemizedOverlay(getIcon());
        
        Intent i = getIntent();
        final int lat = i.getIntExtra(LAT_EXTRA, -1);
        if(lat == -1) {
        	finish();
        }
        final int lon = i.getIntExtra(LON_EXTRA, -1);
        if(lon == -1) {
        	finish();
        }
        final float accuracy = i.getFloatExtra(ACCURACY_EXTRA, -1);
        if(accuracy == -1) {
        	finish();
        }
        final GeoPoint gp = point.add(lat, lon);
        point.populateItem();
    	mapController.setCenter(gp);
        mapOverlays.add(point);
        mapOverlays.add(new Overlay() {
        	@Override
        	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        		Projection projection = mapView.getProjection();
        		if (shadow && projection != null) {
        			Point pt = new Point();
        			projection.toPixels(gp, pt);
        			float circleRadius = metersToRadius(accuracy, projection, gp.getLatitudeE6());
        			Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        			circlePaint.setColor(0x186666ff);
        			circlePaint.setStyle(Style.FILL_AND_STROKE);
        			canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
        					circlePaint);
        			circlePaint.setColor(0xff6666ff);
        			circlePaint.setStyle(Style.STROKE);
        			canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
        					circlePaint);
        			super.draw(canvas, mapView, shadow);
        		}
        	}
		});
        
    }
    
    public int metersToRadius(float m, Projection p, double latitude) {
        return (int) (p.metersToEquatorPixels(m) * (1/ Math.cos(Math.toRadians(latitude))));         
    }
    
    private Drawable getIcon() {
        if(icon == null) {
            icon = getResources().getDrawable(R.drawable.ic_launcher);
        }
        return icon;
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
