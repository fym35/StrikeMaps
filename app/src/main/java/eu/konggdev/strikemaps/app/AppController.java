package eu.konggdev.strikemaps.app;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import eu.konggdev.strikemaps.MainActivity;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

import static android.content.Context.MODE_PRIVATE;
public class AppController {
    private final MainActivity appActivity;

    private MapComponent map;
    private UIComponent ui;
    public AppController(MainActivity refActivity) {
        appActivity = refActivity;
    }
    public void logcat(String log) {
        appActivity.logcat(log);
    }
    public UIComponent getUi() {
        if (ui == null) init();
        return ui;
    }
    public MapComponent getMap() {
        if (map == null) init();
        return map;
    }
    public SharedPreferences getPrefs() {
        return getActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
    }
    public AppCompatActivity getActivity() { return appActivity; }
    public void init() {
        if (getActivity().getSupportActionBar() != null)
            getActivity().getSupportActionBar().show();
        if(map == null) map = new MapComponent(this);
        if(ui == null) {
            ui = new UIComponent(this, map);
            ui.swapScreen(DefinedScreen.MAIN); //Initial
        }
    }
}
