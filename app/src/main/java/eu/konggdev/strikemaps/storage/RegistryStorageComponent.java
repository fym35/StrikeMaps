package eu.konggdev.strikemaps.storage;

import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.helper.UserPrefsHelper;

import java.util.Map;

public class RegistryStorageComponent implements Component {
    private AppController app;

    private Map<Integer, MapStyle> styles;

    private Map<Integer, MapSource> sources;

    public RegistryStorageComponent(AppController app) {
        this.app = app;
        styles();
    }

    private Map<Integer, MapSource> sources() {
        if (sources == null) sources = UserPrefsHelper.sources(app.getPrefs());
        return sources;
    }

    public Map<Integer, MapSource> getSources() {
        return sources();
    }

    private Map<Integer, MapStyle> styles() {
        if (styles == null) styles = UserPrefsHelper.styles(app.getPrefs(), app);
        return styles;
    }

    public Map<Integer, MapStyle> getStyles() {
        return styles();
    }

    public MapStyle getStyle(Integer id) {
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
        UserPrefsHelper.styles(app.getPrefs(), styles);
    }

    public void checkForUpdates() {
        //
    }
}