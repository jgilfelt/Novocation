package com.novoda.locationdemo.activity.location;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {

    private static final String EMPTY_STRING = "";
    
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    
    public LocationItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }

    @Override
    protected OverlayItem createItem(int paramInt) {
        return mOverlays.get(paramInt);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    public GeoPoint add(int latitude, int longitude) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        OverlayItem overlayitem = new OverlayItem(point, EMPTY_STRING, EMPTY_STRING);
        mOverlays.add(overlayitem);
        return point;
    }
    
    public void populateItem() {
        populate();
    }
    
    public void clear() {
        mOverlays.clear();
    }
    
}
