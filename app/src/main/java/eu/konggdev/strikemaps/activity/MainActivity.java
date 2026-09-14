package eu.konggdev.strikemaps.activity;

import android.content.SharedPreferences;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;

import android.os.Bundle;

import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.element.region.UIRegion;
import eu.konggdev.strikemaps.ui.element.region.content.MainContentRegion;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutControls;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutSearch;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentOfflineMaps;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentSettings;
import eu.konggdev.strikemaps.ui.screen.Screen;
import java.util.List;
import java.util.Map;

public class MainActivity extends ComponentHolderActivity {
    private List<Component> components;
    private UIComponent ui;

    private Map<String, Screen> initScreens(UIComponent ui, MapComponent map, RegistryStorageComponent registry, SharedPreferences userPrefs) {
        return Map.of(
                //Main screen
                "main", new Screen(
                        this,
                        //Main screen init regions definition
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(map.toFragment(), R.id.mainContentView),
                                R.id.bottomUi, new UIRegion(new FragmentLayoutControls(this, ui, map, registry, userPrefs, R.id.bottomUi), R.id.bottomUi),
                                R.id.topUi, new UIRegion(new FragmentLayoutSearch(this, ui, R.id.topUi), R.id.topUi)
                        ) //TODO: Probably stop referencing layout 3(!) times everytime
                ),
                //Settings screen
                "settings", new Screen(
                        this,
                        //Just the settings content fragment
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(new FragmentLayoutContentSettings(this, ui, userPrefs), R.id.mainContentView)
                        )
                ),
                //Offline maps screen
                "offline", new Screen(
                        this,
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(new FragmentLayoutContentOfflineMaps(this), R.id.mainContentView)
                        )
                )
        );
    }

    @NonNull
    private List<Component> initComponents() {
        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        RegistryStorageComponent registry = new RegistryStorageComponent(this, userPrefs);
        MapComponent map = new MapComponent(this, registry, userPrefs);
        UIComponent ui = new UIComponent(this, map, registry, userPrefs);
        ui.defineScreens(initScreens(ui, map, registry, userPrefs));
        this.ui = ui;
        return List.of(
                map,
                ui,
                registry
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.components = initComponents();
        ui.swapScreen("main");
        setContentView(R.layout.view_main);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!ui.back()) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}