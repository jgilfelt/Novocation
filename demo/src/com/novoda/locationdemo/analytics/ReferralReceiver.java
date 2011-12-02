package com.novoda.locationdemo.analytics;

import com.google.android.apps.analytics.AnalyticsReceiver;

import android.content.Context;
import android.content.Intent;

public class ReferralReceiver extends AnalyticsReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		new Analytics(context).trackInstall();
	}
}
