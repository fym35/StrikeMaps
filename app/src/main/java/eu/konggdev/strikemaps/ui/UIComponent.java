package eu.konggdev.strikemaps.ui;

import android.app.AlertDialog;
import android.view.View;
import androidx.annotation.NonNull;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.ui.element.region.content.MainContentRegion;
import eu.konggdev.strikemaps.ui.element.region.UIRegion;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutControls;
import eu.konggdev.strikemaps.ui.fragment.layout.FragmentLayoutSearch;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentSettings;
import eu.konggdev.strikemaps.ui.screen.Screen;
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.function.Consumer;

public class UIComponent implements Component {
    @NonNull AppController app;
    MapComponent map;

    private final ArrayDeque<Screen> screenStack = new ArrayDeque<>();

    public UIComponent(AppController app, MapComponent map) {
        this.app = app;
        this.map = map;
    }

    public Map<DefinedScreen, Screen> getScreens(MapComponent map) {
        return Map.of(
                //Main screen
                DefinedScreen.MAIN, new Screen(
                        //App reference
                        app,
                        //Main screen init regions definition
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(map.toFragment(), R.id.mainContentView),
                                R.id.bottomUi, new UIRegion(new FragmentLayoutControls(app, R.id.bottomUi), R.id.bottomUi),
                                R.id.topUi, new UIRegion(new FragmentLayoutSearch(app, R.id.topUi), R.id.topUi)
                        ) //TODO: Probably stop referencing layout 3(!) times everytime
                ),
                //Settings screen
                DefinedScreen.SETTINGS, new Screen(
                        app,
                        //Just the settings content fragment
                        Map.of(
                                R.id.mainContentView, new MainContentRegion(new FragmentLayoutContentSettings(), R.id.mainContentView)
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

    public <T> void alert(AlertDialog dialog, Consumer<T> callback) {
        dialog.show();
    }

    public View inflateUi(int layout) {
        return app.getActivity().getLayoutInflater().inflate(layout, null);
    }
}