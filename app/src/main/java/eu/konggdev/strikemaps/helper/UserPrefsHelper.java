package eu.konggdev.strikemaps.helper;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.source.MapSource.MapSourceContractType;
import eu.konggdev.strikemaps.map.source.model.TileSource;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.map.style.document.StyleDocument;
import eu.konggdev.strikemaps.map.style.management.StyleManagementMetadata;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class UserPrefsHelper {
    private UserPrefsHelper() {} // prevent instantiation

    //Keys
    private static final String KEY_STARTUP_MAP_STYLE = "startupMapStyle";
    private static final String KEY_MAP_RENDERER = "mapRenderer";
    private static final String KEY_PERSIST_LOCATION_ENABLED = "persistLocationEnabled";
    private static final String KEY_LAST_LOCATION_ENABLED = "lastLocationEnabled";
    private static final String KEY_STYLES = "styles";
    private static final String KEY_SOURCES = "sources";

    //Defaults
    private static final Integer DEFAULT_MAP_STYLE = 0;
    private static final Integer DEFAULT_MAP_RENDERER = 0;
    private static final boolean DEFAULT_PERSIST_LOCATION_ENABLED = true;
    private static final boolean DEFAULT_LAST_LOCATION_ENABLED = false;

    public static Map<Integer, MapStyle> DEFAULT_STYLES(AppController app) {
        Map<Integer, MapStyle> styles = new HashMap<>();
        String[] styleAssets = FileHelper.getAssetFiles("bundled/style", ".style.json", app);
        for (int i = 0; i < styleAssets.length; i++) { styles.put( i,
                new MapStyle(
                        FileHelper.loadStringFromAssetFile(styleAssets[i], app),
                        new StyleOptions(),
                        new StyleManagementMetadata()
                ));
        }
        return styles;
    } //Built-in Styles

    private static final Map<Integer, MapSource> DEFAULT_SOURCES = Map.of(
            0, new MapSource(
                    MapSourceContractType.DEFINITION,
                    "Strike Maps Planet",
                    new TileSource("https://tiles.strikemaps.eu/planet"),
                    "vector",
                    "smts"
            ),
            1, new MapSource(
                    MapSourceContractType.DEFINITION,
                    "ArcGIS Imagery",
                    new TileSource(new String[]{"https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"}),
                    "raster",
                    "raster"
            )
    ); //Built-in Sources

    public static Integer startupMapStyle(SharedPreferences prefs) {
        return prefs.getInt(KEY_STARTUP_MAP_STYLE, DEFAULT_MAP_STYLE);
    }

    public static boolean startupMapStyle(SharedPreferences prefs, Integer updated) {
        return prefs.edit().putInt(KEY_STARTUP_MAP_STYLE, updated).commit();
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

    public static Map<Integer, MapStyle> styles(SharedPreferences prefs, AppController app) {
        String json = prefs.getString(KEY_STYLES, null);
        if (json == null) return DEFAULT_STYLES(app);
        Type type = new TypeToken<Map<Integer, MapStyle.StoredRepresentation>>() {}.getType();
        Map<Integer, MapStyle.StoredRepresentation> stored =
                new Gson().fromJson(json, type);
        Map<Integer, MapStyle> result = new HashMap<>();
        for (var entry : stored.entrySet())
            result.put(entry.getKey(), entry.getValue().restore());
        return result;
    }

    public static boolean styles(SharedPreferences prefs, Map<Integer, MapStyle> updated) {
        Map<Integer, MapStyle.StoredRepresentation> stored = new HashMap<>();
        for (var entry : updated.entrySet())
            stored.put(entry.getKey(), entry.getValue().makeStoredRepresentation());
        return prefs.edit()
                .putString(KEY_STYLES, new Gson().toJson(stored))
                .commit();
    }

    public static Map<Integer, MapSource> sources(SharedPreferences prefs) {
        String json = prefs.getString(KEY_SOURCES, null);
        if (json == null) return DEFAULT_SOURCES;
        Type type = new TypeToken<Map<Integer, MapSource>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static boolean sources(SharedPreferences prefs, Map<Integer, MapSource> updated) {
        return prefs.edit()
                .putString(KEY_SOURCES, new Gson().toJson(updated))
                .commit();
    }
}
