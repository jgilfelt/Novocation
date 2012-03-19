/**
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
