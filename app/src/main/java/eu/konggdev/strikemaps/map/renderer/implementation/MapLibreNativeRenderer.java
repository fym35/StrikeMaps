package eu.konggdev.strikemaps.map.renderer.implementation;

import android.view.View;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.konggdev.strikemaps.app.util.JsonPatcher;
import eu.konggdev.strikemaps.data.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import eu.konggdev.strikemaps.map.style.MapStyle;
import org.maplibre.android.MapLibre;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.geojson.Feature;

import java.util.List;

import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;

public class MapLibreNativeRenderer implements MapRenderer, OnMapReadyCallback {
    @NonNull AppController app;
    @NonNull MapComponent controller;
    MapLibreMap map;
    final MapView mapView;

    private JsonNode origin;

    public MapLibreNativeRenderer(AppController app, MapComponent controller) {
        this.app = app;
        this.controller = controller;
        MapLibre.getInstance(app.getActivity());
        this.mapView = new MapView(app.getActivity());
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    @Override
    public void styleUpdate(MapStyle style) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //Thanks to this, styleUpdate(null) can be used as an origin reload
        if (style != null) {
            try {
                ObjectNode root = style.metadata.deepCopy();

                //Sources
                ObjectNode sources = mapper.createObjectNode();
                style.sources.forEach((k, v) -> sources.set(k, mapper.valueToTree(v)));

                //Layers
                ArrayNode layers = mapper.createArrayNode();
                layers.addAll((ArrayNode) style.layerDefinitions);

                //Set all to root
                root.set("sources", sources);
                root.set("layers", layers);
                this.origin = root;
            } catch (Exception e) {
                app.logcat("Failed to parse style: " + style.name);
                e.printStackTrace();
            }
        }

        try {
            map.setStyle(new Style.Builder().fromJson(mapper.writeValueAsString(origin)));
        } catch (Exception e) {
            app.logcat("Failed to set style: " + style.name);
            e.printStackTrace();
        }

        //Since we just annihilated all overlays from the face of the earth, lets repatch them
        if (controller.overlays != null) {
            for(MapOverlay overlay : controller.overlays.values())
                overlayUpdate(overlay);
        }
    }

    @Override
    public void overlayUpdate(MapOverlay overlay) {
        if(map == null) return;

        Style style = map.getStyle();
        ObjectMapper mapper = new ObjectMapper();

        if(style == null) return;

        try {
            JsonNode current = mapper.readTree(style.getJson());

            JsonNode merged;
            if (controller.hasOverlay(overlay)) {
                merged = JsonPatcher.patch(current, overlay.makePatch());
            } else {
                merged = JsonPatcher.unpatch(origin, current, overlay.makePatch());
            }

            map.setStyle(new Style.Builder().fromJson(mapper.writeValueAsString(merged)));
        } catch (Exception e) {
            app.logcat("Failed to patch overlay: " + overlay.toString());
            e.printStackTrace();
        }
    }

    @Override
    public View getView() {
        return mapView;
    }

    @Override
    public List<Feature> featuresAtPoint(LatLng point) {
        return map.queryRenderedFeatures(map.getProjection().toScreenLocation(point));
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap maplibreMap) {
        this.map = maplibreMap;

        controller.setStyle(MapStyle.fromJsonFile(UserPrefsHelper.startupMapStyle(app.getPrefs()), app));

        //I have my own implementation of attribution that credits MapLibre among others, it's not as bad as it looks :)
        map.getUiSettings().setLogoEnabled(false);
        map.getUiSettings().setAttributionEnabled(false);

        map.addOnMapClickListener(point -> controller.onMapClick(point));
        map.addOnMapLongClickListener(point -> controller.onMapLongClick(point));
    }
}
