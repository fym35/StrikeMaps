package eu.konggdev.strikemaps.map.renderer.implementation;

import android.view.View;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.geojson.Feature;

import java.util.Collections;
import java.util.List;

//Stub for now
public class MapLibreGLJSRenderer implements MapRenderer {
    @Override
    public void reload() {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public List<Feature> featuresAtPoint(LatLng point) {
        return Collections.emptyList();
    }

    public MapLibreGLJSRenderer(AppController app, MapComponent controller) { }
}
