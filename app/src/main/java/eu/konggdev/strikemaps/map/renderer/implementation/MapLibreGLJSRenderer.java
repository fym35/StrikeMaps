package eu.konggdev.strikemaps.map.renderer.implementation;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.app.util.JsonPatcher;
import eu.konggdev.strikemaps.map.MapComponent;
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
    @NonNull AppController app;

    @NonNull MapComponent controller;

    final WebView webView;

    private JsonNode origin;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public MapLibreGLJSRenderer(AppController app, MapComponent controller) {
        this.app = app;
        this.controller = controller;
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

        webView.loadUrl("file:///android_asset/library/maplibre/gl-js/index.html");
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
                if (style.sources != null)
                    style.sources.forEach((k, v) -> sources.set(k, mapper.valueToTree(v)));

                //Layers
                ArrayNode layers = mapper.createArrayNode();
                if (style.layerDefinitions != null)
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
            final String mapped = mapper.writeValueAsString(origin);
            webView.evaluateJavascript("map.setStyle(" + mapped + ", { diff: false });", null);
            webView.evaluateJavascript("map.redraw()", null); //Force redraw to make the style change visible
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
        ObjectMapper mapper = new ObjectMapper();
        webView.evaluateJavascript(
                "JSON.stringify(map.getStyle())",
                style -> {
                    try {
                        JsonNode current = mapper.readTree(style);

                        JsonNode merged;
                        if (controller.hasOverlay(overlay)) {
                            merged = JsonPatcher.patch(current, overlay.makePatch());
                        } else {
                            merged = JsonPatcher.unpatch(origin, current, overlay.makePatch());
                        }

                        final String mapped = mapper.writeValueAsString(merged);
                        webView.evaluateJavascript("map.setStyle(" + mapped + ", { diff: false });", null);
                        webView.evaluateJavascript("map.redraw()", null); //Force redraw to make the style change visible
                    } catch (Exception e) {
                        app.logcat("Failed to patch overlay: " + overlay.toString());
                        e.printStackTrace();
                    }
                }
        );

    }


    @Override
    public View getView() {
        return webView;
    }

    @Override
    public List<Feature> featuresAtPoint(LatLng point) {
        return Collections.emptyList();
    }

    class Bridge { }
}
