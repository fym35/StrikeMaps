package eu.konggdev.strikemaps.data.helper;

import android.content.SharedPreferences;

public final class UserPrefsHelper {
    private UserPrefsHelper() {} // prevent instantiation

    //Keys
    private static final String KEY_STARTUP_MAP_STYLE = "startupMapStyle";
    private static final String KEY_MAP_RENDERER = "mapRenderer";
    private static final String KEY_PERSIST_LOCATION_ENABLED = "persistLocationEnabled";
    private static final String KEY_LAST_LOCATION_ENABLED = "lastLocationEnabled";

    //Defaults
    private static final String DEFAULT_MAP_STYLE = "bundled/style/classic.style.json";
    private static final Integer DEFAULT_MAP_RENDERER = 0;
    private static final boolean DEFAULT_PERSIST_LOCATION_ENABLED = true;
    private static final boolean DEFAULT_LAST_LOCATION_ENABLED = false;

    public static String startupMapStyle(SharedPreferences prefs) {
        return prefs.getString(KEY_STARTUP_MAP_STYLE, DEFAULT_MAP_STYLE);
    }

    public static boolean startupMapStyle(SharedPreferences prefs, String updated) {
        return prefs.edit().putString(KEY_STARTUP_MAP_STYLE, updated).commit();
    }

    public static Integer mapRenderer(SharedPreferences prefs) {
        return prefs.getInt(KEY_MAP_RENDERER, DEFAULT_MAP_RENDERER);
    }

    public static boolean mapRenderer(SharedPreferences prefs, Integer updated) {
        return prefs.edit().putInt(KEY_MAP_RENDERER, updated).commit();
    }

    public static boolean persistLocationEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_PERSIST_LOCATION_ENABLED, DEFAULT_PERSIST_LOCATION_ENABLED);
    }

    public static boolean lastLocationEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_LAST_LOCATION_ENABLED, DEFAULT_LAST_LOCATION_ENABLED);
    }

    public static boolean lastLocationEnabled(SharedPreferences prefs, boolean status) {
        return prefs.edit().putBoolean(KEY_LAST_LOCATION_ENABLED, status).commit();
    }
}
