package eu.konggdev.strikemaps.storage;

import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.helper.UserPrefsHelper;

import java.util.Map;

public class RegistryStorageComponent implements Component {
    private final ComponentHolderActivity activity;

    private final SharedPreferences userPrefs;

    private Map<Integer, MapStyle> styles;

    private Map<Integer, MapSource> sources;

    public RegistryStorageComponent(ComponentHolderActivity activity, SharedPreferences userPrefs) {
        this.activity = activity;
        this.userPrefs = userPrefs;
        initAll();
    }

    private Map<Integer, MapSource> sources() {
        if (sources == null) sources = UserPrefsHelper.sources(userPrefs);
        return sources;
    }

    public Map<Integer, MapSource> getSources() {
        return sources();
    }

    @Nullable public MapSource getSource(Integer id) {
        return sources().get(id);
    }

    private Map<Integer, MapStyle> styles() {
        if (styles == null) styles = UserPrefsHelper.styles(userPrefs, activity);
        return styles;
    }

    public Map<Integer, MapStyle> getStyles() {
        return styles();
    }

    @Nullable public MapStyle getStyle(Integer id) {
        return styles().get(id);
    }

    public int addStyle(MapStyle style) {
        Map<Integer, MapStyle> styles = styles();
        int id = styles.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1) + 1;

        styles.put(id, style);
        save();
        return id;
    }

    public void updateStyle(int id, MapStyle style) {
        styles().put(id, style);
        save();
    }

    public void deleteStyle(int id) {
        styles().remove(id);
        save();
    }

    private void save() {
        UserPrefsHelper.styles(userPrefs, styles);
    }

    public void checkForUpdates() {
        //
    }

    private void initAll() {
        styles(); sources();
    }
}