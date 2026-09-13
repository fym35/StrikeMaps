package eu.konggdev.strikemaps.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import androidx.activity.OnBackPressedCallback;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;

import android.os.Bundle;

import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

import java.util.List;

public class MainActivity extends ComponentHolderActivity {
    private List<Component> components;
    private UIComponent ui;

    private List<Component> initComponents() {
        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        RegistryStorageComponent registry = new RegistryStorageComponent(this, userPrefs);
        MapComponent mapComponent = new MapComponent(this, registry, userPrefs);
        this.ui = new UIComponent(this, mapComponent, registry, userPrefs, DefinedScreen.MAIN);
        return List.of(
                mapComponent,
                ui,
                registry
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        components = initComponents();
        setContentView(R.layout.view_main);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (ui.back()) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}