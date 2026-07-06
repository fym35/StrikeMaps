package eu.konggdev.strikemaps.map.renderer;

import android.view.View;

import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.style.MapStyle;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.geojson.Feature;

import java.util.List;

public interface MapRenderer {
    void styleUpdate(MapStyle style);

    void overlayUpdate(MapOverlay overlay);

    View getView();

    //TODO: Get rid of MapLibre Feature class dependence
    List<Feature> featuresAtPoint(LatLng point);
}
