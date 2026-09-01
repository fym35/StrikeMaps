package eu.konggdev.strikemaps.map;

import java.util.*;

import android.widget.Toast;
import eu.konggdev.strikemaps.Component;
import eu.konggdev.strikemaps.map.renderer.implementation.MapLibreGLJSRenderer;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.map.renderer.implementation.VtmRenderer;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.geojson.Feature;

import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.renderer.implementation.MapLibreNativeRenderer;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import eu.konggdev.strikemaps.ui.fragment.layout.content.main.FragmentLayoutContentMap;

public class MapComponent implements Component {
    private final MapRenderer mapRenderer;
    private final AppController app;

    public MapStyle style;
    public Map<Class<? extends MapOverlay>, MapOverlay> overlays = new HashMap<>();

    public MapComponent(AppController ref) {
        this.app = ref;
        switch(UserPrefsHelper.mapRenderer(app.getPrefs())) {
            case 0:
                this.mapRenderer = new MapLibreGLJSRenderer(app, this);
                break;
            case 1:
                this.mapRenderer = new MapLibreNativeRenderer(app, this);
                break;
            case 2:
                this.mapRenderer = new VtmRenderer(app, this);
                break;
            default: //This shouldn't happen
                Toast.makeText(app.getActivity(), "Invalid renderer value in preferences\nFalling back to MapLibre GL JS", Toast.LENGTH_SHORT).show();
                this.mapRenderer = new MapLibreGLJSRenderer(app, this);
                break;
        };
    }

    public FragmentLayoutContentMap toFragment() {
        return new FragmentLayoutContentMap(mapRenderer.getView());
    }

    public void setStyle(MapStyle style) {
        this.style = style;
        mapRenderer.styleUpdate(style.effectiveDocument());
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
                app.getUi().alert(
                    AlertDialogFactory.pointSelector(app, features, selectedItem -> {
                        selectPoint(selectedItem);
                    }));
        }
        return true;
    }

    public boolean onMapLongClick(LatLng point) {
        //TODO: Likely Nonfeature(?) point selection
        return true;
    }

    public void onMapInit() {
        setStyle(
                app.getRegistry().getStyle(
                        UserPrefsHelper.startupMapStyle(app.getPrefs())
                )
        );
    }
}
