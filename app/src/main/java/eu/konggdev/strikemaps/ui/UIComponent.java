package eu.konggdev.strikemaps.ui;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

import java.util.ArrayDeque;
import java.util.Map;

public class UIComponent implements Component {
    private final ComponentHolderActivity activity;
    private final MapComponent map;

    private final RegistryStorageComponent registry;

    private final SharedPreferences userPrefs;

    private final ArrayDeque<Screen> screenStack = new ArrayDeque<>();

    public UIComponent(ComponentHolderActivity activity, MapComponent map, RegistryStorageComponent registry, SharedPreferences userPrefs, DefinedScreen firstScreen) {
        this.activity = activity;
        this.map = map;
        this.registry = registry;
        this.userPrefs = userPrefs;
        swapScreen(firstScreen);
    }

    public Map<DefinedScreen, Screen> getScreens(MapComponent map) {
        return Map.of(
                //Main screen
                DefinedScreen.MAIN, new Screen(
                        activity,
                        //Main screen init regions definition
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(map.toFragment(), R.id.mainContentView),
                                R.id.bottomUi, new UIRegion(new FragmentLayoutControls(activity, this, map, registry, userPrefs, R.id.bottomUi), R.id.bottomUi),
                                R.id.topUi, new UIRegion(new FragmentLayoutSearch(activity, this, R.id.topUi), R.id.topUi)
                        ) //TODO: Probably stop referencing layout 3(!) times everytime
                ),
                //Settings screen
                DefinedScreen.SETTINGS, new Screen(
                        activity,
                        //Just the settings content fragment
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(new FragmentLayoutContentSettings(activity, this, userPrefs), R.id.mainContentView)
                        )
                ),
                //Offline maps screen
                DefinedScreen.OFFLINE, new Screen(
                        activity,
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(new FragmentLayoutContentOfflineMaps(activity), R.id.mainContentView)
                        )
                )
        );
    }

    public void swapScreen(DefinedScreen screenKey) {
        if (!screenStack.isEmpty()) getCurrentScreen().detachAll();
        screenStack.add(getScreens(map).get(screenKey));
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