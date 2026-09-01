package eu.konggdev.strikemaps.map.renderer.implementation;

import android.view.View;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import eu.konggdev.strikemaps.map.style.document.StyleDocument;
import okhttp3.OkHttpClient;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.geojson.Feature;
import org.oscim.android.MapView;
import org.oscim.map.Map;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.util.Collections;
import java.util.List;

public class VtmRenderer implements MapRenderer {

    AppController app;
    MapComponent controller;

    Map map;
    final MapView mapView;

    public VtmRenderer(AppController app, MapComponent controller) {
        this.app = app;
        this.controller = controller;
        this.mapView = new MapView(app.getActivity());
        this.map = mapView.map();
    }

    @Override
    public void overlayUpdate(MapOverlay overlay) {
        //TODO
    }

    @Override
    public void styleUpdate(StyleDocument style) {
        //TODO
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OSciMap4TileSource tileSource = OSciMap4TileSource.builder().httpFactory(new OkHttpEngine.OkHttpFactory(builder)).build();

        map.setBaseMap(tileSource);
    }

    @Override
    public View getView() {
        return mapView;
    }

    @Override
    public List<Feature> featuresAtPoint(LatLng point) {
        return Collections.emptyList();
    }
}