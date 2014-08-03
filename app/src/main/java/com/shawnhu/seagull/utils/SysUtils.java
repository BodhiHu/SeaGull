package com.shawnhu.seagull.utils;

import java.io.IOException;

/**
 * Created by shawnhu on 8/3/14.
 */
public class SysUtils {


	public static boolean isMIUI() {
        final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
        final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
        final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
					|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
					|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
		} catch (final IOException e) {
			return false;
		}
	}
}
