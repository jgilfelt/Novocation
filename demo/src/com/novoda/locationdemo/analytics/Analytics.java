package com.novoda.locationdemo.analytics;

import android.content.Context;
import android.location.Location;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Analytics {

	private static final String ANALYTICS_UID = "UA-24812858-2";

	private static interface PageView {
		String locationUpdateList = "/locationUpdateList";
		String install = "/install";
	}

	private GoogleAnalyticsTracker tracker;

	public Analytics(Context context) {
		if (tracker != null) {
			return;
		}
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(ANALYTICS_UID, 20, context);
	}

	public void stopSession() {
		tracker.stopSession();
	}

	public void trackLocationUpdateList() {
		trackPageView(PageView.locationUpdateList);
	}
	
	public void trackInstall() {
		trackPageView(PageView.install);
	}
	
	public void trackLocationReceived(Location location, Location currentLocation, long time) {
		long deltaTimeToGetLocation = System.currentTimeMillis() - time;
		long deltaTimeToPreviousLocation = System.currentTimeMillis() - location.getTime();
		float accuracy = location.getAccuracy();
		
		String age = getAgeOfLocation(deltaTimeToPreviousLocation);
		String accuracyIndicator = getAccuracyIndicator(accuracy);
		String counter = isFirstOrAnother(currentLocation);
		String in = getTimeIndicator(deltaTimeToGetLocation);
		
		trackPageView("/" + counter + "_location_in_" + in + "_accuracy_" + accuracyIndicator + "_age_" + age);
	}
	
	public void trackLocationSuccessOrFailure(Location location, long time) {
		long deltaTimeToGetLocation = System.currentTimeMillis() - time;
		if(location == null) {
			trackPageView("/unable_to_get_location_" + getTimeIndicator(deltaTimeToGetLocation));
			trackFailure(deltaTimeToGetLocation);
			return;
		}
		String accuracy = getAccuracyIndicator(location.getAccuracy());
		trackPageView("/on_pause_got_location_" + getTimeIndicator(deltaTimeToGetLocation) + "_accuracy_" + accuracy);
		if(isFailure(location, deltaTimeToGetLocation)) {
			trackPageView("/not_accurate");
		} else {			
			trackPageView("/success");
		}
	}
	
	public void trackNegativeFeedback() {
		trackPageView("/negativeFeedback");
	}

	public void trackPositiveFeedback() {
		trackPageView("/positiveFeedback");
	}

	private boolean isFailure(Location location, long deltaTimeToGetLocation) {
		return deltaTimeToGetLocation < 2*60*1000 && location.getAccuracy() > 200;
	}

	private void trackFailure(long deltaTimeToGetLocation) {
		if(deltaTimeToGetLocation > 60000) {
			trackPageView("/failure");
		}
	}

	private String getAgeOfLocation(long deltaTimeToPreviousLocation) {
		String age = "infinity";
		if(deltaTimeToPreviousLocation < 30000) {
			age = "<30s";
		} else if (deltaTimeToPreviousLocation < 60000) {
			age = "<1m";
		} else if (deltaTimeToPreviousLocation < 5*60*1000) {
			age = "<5m";
		} else if (deltaTimeToPreviousLocation < 30*60*1000) {
			age = "<30m";
		}
		return age;
	}

	private String getAccuracyIndicator(float accuracy) {
		String accuracyIndicator = ">200";
		if(accuracy < 50) {
			accuracyIndicator = "<50";
		} else if (accuracy < 100) {
			accuracyIndicator = ">100";
		}
		return accuracyIndicator;
	}

	private String isFirstOrAnother(Location currentLocation) {
		String counter = "another"; 
		if(currentLocation == null) {
			counter = "first";
		}
		return counter;
	}

	private String getTimeIndicator(long deltaTimeToGetLocation) {
		String in = ">2m"; 
		if(deltaTimeToGetLocation <= 30000) {
			in = "<30s";
		} else if(deltaTimeToGetLocation <= 60000) {
			in = "<1m";
		} else if(deltaTimeToGetLocation <= 90000) {
			in = "<90s";
		} else if(deltaTimeToGetLocation <= 120000) {
			in = "<2m";
		}
		return in;
	}

	private void trackPageView(String page) {
		tracker.trackPageView(page);
	}

}
