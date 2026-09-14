package eu.konggdev.strikemaps.ui;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.ui.element.region.content.MainContentRegion;
import eu.konggdev.strikemaps.ui.element.region.UIRegion;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutControls;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutSearch;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentOfflineMaps;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentSettings;
import eu.konggdev.strikemaps.ui.screen.Screen;

import java.util.ArrayDeque;
import java.util.Map;

public class UIComponent implements Component {
    private final String TAG = "UIComponent";

    private final ComponentHolderActivity activity;
    private final MapComponent map;

    private final RegistryStorageComponent registry;

    private final SharedPreferences userPrefs;

    private final ArrayDeque<Screen> screenStack = new ArrayDeque<>();

    private Map<String, Screen> screens;

    public UIComponent(ComponentHolderActivity activity, MapComponent map, RegistryStorageComponent registry, SharedPreferences userPrefs) {
        this.activity = activity;
        this.map = map;
        this.registry = registry;
        this.userPrefs = userPrefs;
    }

    public void defineScreens(Map<String, Screen> screens) {
        this.screens = screens;
    }

    public void swapScreen(String screenKey) {
        if (screens == null) return;
        Screen newScreen = screens.get(screenKey);
        if (newScreen == null) {
            Log.e(TAG, "Invalid screen " + screenKey + " invoked");
            return;
        }
        if (!screenStack.isEmpty()) getCurrentScreen().detachAll();
        screenStack.add(newScreen);
        getCurrentScreen().attachAll();
    }

    public boolean back() {
        if (screenStack.size() <= 1) return false;
        getCurrentScreen().detachAll();

        screenStack.removeLast();
        getCurrentScreen().attachAll();
        return true;
    }

    public Screen getCurrentScreen() {
        return screenStack.getLast();
    }

    public void alert(AlertDialog dialog) {
        dialog.show();
    }

    public View inflateUi(int layout) {
        return activity.getLayoutInflater().inflate(layout, null);
    }
}