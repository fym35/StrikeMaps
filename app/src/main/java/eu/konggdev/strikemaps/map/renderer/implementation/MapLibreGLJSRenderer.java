package eu.konggdev.strikemaps.map.renderer.implementation;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.layer.SourcedMapLayer;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;
import eu.konggdev.strikemaps.map.renderer.MapRenderer;
import eu.konggdev.strikemaps.map.style.MapStyle;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.Style;
import org.maplibre.geojson.Feature;

import java.util.Collections;
import java.util.List;

//Stub for now
public class MapLibreGLJSRenderer implements MapRenderer {

    AppController app;

    MapComponent controller;

    final WebView webView;

    @Override
    public void reload() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        MapStyle style = controller.style;
        try {
	        /* Take metadata from MapStyle
	        everything outside sources, layers */
            ObjectNode root = style.metadata.deepCopy();

            //Sources
            ObjectNode sources = mapper.createObjectNode();
            style.sources.forEach((k, v) -> sources.set(k, mapper.valueToTree(v)));

            //Layers
            ArrayNode layers = mapper.createArrayNode();
            layers.addAll((ArrayNode) style.layerDefinitions);

            //Overlays
            for (MapOverlay overlay : controller.overlays.values()) {
                SourcedMapLayer overlayLayer = overlay.makeLayer();
                sources.set(overlayLayer.key, mapper.valueToTree(overlayLayer.source));
                layers.addAll((ArrayNode) overlayLayer.layer);
            }

            //Set all to root
            root.set("sources", sources);
            root.set("layers", layers);

            webView.evaluateJavascript(
                    "",
                    null
            );
        } catch (Exception e) {
            app.logcat("Failed to reload Map");
            e.printStackTrace();
        }
    }

    @Override
    public View getView() {
        return webView;
    }

    @Override
    public List<Feature> featuresAtPoint(LatLng point) {
        return Collections.emptyList();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public MapLibreGLJSRenderer(AppController app, MapComponent controller) {
        webView = new WebView(app.getActivity());
        webView.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.addJavascriptInterface(new Bridge(), "AndroidBridge");

        webView.loadUrl("file:///android_asset/maplibre/gl-js/index.html");
    }

    class Bridge { }
}
