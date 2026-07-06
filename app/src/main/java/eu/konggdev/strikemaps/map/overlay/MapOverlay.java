package eu.konggdev.strikemaps.map.overlay;

import com.fasterxml.jackson.databind.JsonNode;

/* More or less a data-driven layer factory */
public interface MapOverlay {
    public JsonNode makePatch();
}
