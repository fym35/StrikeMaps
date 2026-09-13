package eu.konggdev.strikemaps.map.overlay.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;

public class PointSelectionOverlay implements MapOverlay {
    ComponentHolderActivity app;
    MapComponent map;
    @Override
    public JsonNode makePatch() {
        return null;
    }
}
