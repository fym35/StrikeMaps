package eu.konggdev.strikemaps.map;

import java.util.*;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.map.renderer.implementation.MapLibreGLJSRenderer;
import eu.konggdev.strikemaps.map.style.document.StyleDocument;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.map.renderer.implementation.VtmRenderer;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.geojson.Feature;

import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.renderer.implementation.MapLibreNativeRenderer;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentMap;

public class MapComponent implements Component {
    private final String TAG = "MapComponent";

    private final MapRenderer mapRenderer;

    private final ComponentHolderActivity activity;

    private final RegistryStorageComponent registry;

    private final SharedPreferences userPrefs;

    //Current style
    public Integer styleId;

    public Map<Class<? extends MapOverlay>, MapOverlay> overlays = new HashMap<>();

    public MapComponent(ComponentHolderActivity activity, RegistryStorageComponent registry, SharedPreferences userPrefs) {
        this.activity = activity;
        this.registry = registry;
        this.userPrefs = userPrefs;
        switch(UserPrefsHelper.mapRenderer(userPrefs)) {
            case 0:
                this.mapRenderer = new MapLibreGLJSRenderer(activity, this);
                break;
            case 1:
                this.mapRenderer = new MapLibreNativeRenderer(activity, this);
                break;
            case 2:
                this.mapRenderer = new VtmRenderer(activity, this);
                break;
            default: //This shouldn't happen
                Toast.makeText(activity, "Invalid renderer value in preferences\nFalling back to MapLibre GL JS", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Invalid renderer value in preferences\nFalling back to MapLibre GL JS");
                this.mapRenderer = new MapLibreGLJSRenderer(activity, this);
                break;
        };
    }

    public FragmentLayoutContentMap toFragment() {
        return new FragmentLayoutContentMap(mapRenderer.getView());
    }

    public void setStyle(Integer id) {
        styleId = id;
        //Pass to renderer
        StyleDocument document = registry.getStyle(styleId)
                .effectiveDocument(registry);
        if (document == null) {
            Log.e(TAG, "Received null style from registry");
            return;
        }
        mapRenderer.styleUpdate(document);
    }

    public void switchOverlay(MapOverlay overlay) {
        if (hasOverlay(overlay)) overlays.remove(overlay.getClass());
        else overlays.put(overlay.getClass(), overlay);
        overlayUpdate(overlay);
    }

    public boolean hasOverlay(MapOverlay overlay) {
        return overlays.containsKey(overlay.getClass());
    }

    public boolean hasOverlay(Class<? extends MapOverlay> overlay) {
        return overlays.containsKey(overlay);
    }

    public void overlayUpdate(MapOverlay in) {
        mapRenderer.overlayUpdate(in);
    }

    public void selectPoint(Feature selection) {
        //FIXME: Put back FragmentPointPreviewPopup (private code atm)
    }

    public boolean onMapClick(LatLng point) {
        List<Feature> features = mapRenderer.featuresAtPoint(point);

        switch (features.size()) {
            case 0:
                //TODO: Implement point selection for no POI found (MIGHT be done on long click??)
                //Maybe collapse UI? (Hide/show UI feature)... could be user configurable
                break;
            case 1:
                selectPoint(features.get(0));
                break;
            default:
                UIComponent ui = activity.getComponent(UIComponent.class);
                if (ui != null) {
                    ui.alert(
                            AlertDialogFactory.pointSelector(activity, ui, features, this::selectPoint)
                    );
                }
        }
        return true;
    }

    public boolean onMapLongClick(LatLng point) {
        //TODO: Likely Nonfeature(?) point selection
        return true;
    }

    public void onMapInit() {
        setStyle(
                UserPrefsHelper.startupMapStyle(userPrefs)
        );
    }
}
