package com.novoda.location.util;

import android.os.Build;

public class ApiLevelDetector {

	private static final int API_LEVEL;
	private static final boolean SUPPORTS_GINGERBREAD;
	private static final boolean SUPPORTS_FROYO;
    
	static {
        int apiLevel = -1;
        try {
            apiLevel = Build.VERSION.class.getField("SDK_INT").getInt(null);
        } catch (Exception e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        }
        API_LEVEL = apiLevel;
        SUPPORTS_GINGERBREAD = API_LEVEL >= 9;
        SUPPORTS_FROYO = API_LEVEL >= 8;
    }
	
	public static final boolean supportsGingerbread() {
		return SUPPORTS_GINGERBREAD;
	}
	
	public static final boolean supportsFroyo() {
		return SUPPORTS_FROYO;
	}
	
}
