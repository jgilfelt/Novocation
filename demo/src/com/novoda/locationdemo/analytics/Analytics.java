package com.novoda.locationdemo.analytics;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Analytics {

	private static final String ANALYTICS_UID = "UA-5190493-20";

	private static interface PageView {
		String locationUpdateList = "/locationUpdateList";
		String mapTracking = "/mapTracking";
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

	public void trackMapTracking() {
		trackPageView(PageView.mapTracking);
	}
	
	public void trackInstall() {
		trackPageView(PageView.install);
	}

	private void trackPageView(String page) {
		tracker.trackPageView(page);
	}

}
