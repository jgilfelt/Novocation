package com.novoda.locationdemo.activity.location;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.novoda.location.util.Log;

public class AccuracyCircleOverlay extends Overlay {

	private float accuracy;
	private GeoPoint geoPoint;
	
	public AccuracyCircleOverlay(GeoPoint geoPoint, float accuracy) {
		this.accuracy = accuracy;
		this.geoPoint = geoPoint;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		if (shadow && projection == null) {
			Log.v("drawing not done because shadow and projection are null");
			return;
		}
		Point pt = new Point();
		projection.toPixels(geoPoint, pt);
		float circleRadius = metersToRadius(accuracy, projection, geoPoint.getLatitudeE6());
		
		Log.v("Circle Radius : " + circleRadius);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0x186666ff);
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius, paint);
		
		paint.setColor(0xff6666ff);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius, paint);
		
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawCircle((float) pt.x, (float) pt.y, 3, paint);
		
		super.draw(canvas, mapView, shadow);
	}
	
    private int metersToRadius(float m, Projection p, double latitude) {
        return Math.abs((int) (p.metersToEquatorPixels(m) * (1/ Math.cos(Math.toRadians(latitude)))));         
    }
}
