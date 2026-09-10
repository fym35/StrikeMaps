package eu.konggdev.strikemaps.map.style.source;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.JsonNode;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.source.properties.SourceProperties;

// Stores a source currently used by a style
// Along with requirements of what source the style needs here
public class StyleSource {
    public String key;

    // Requirement properties
    public SourceProperties properties;

    // In an effective style, this is the actual source matched to the
    // style's requirements according to user's preferences (StyleOptions).
    // Otherwise, this is the fallback source.
    // It also preserves the default source defined by the JSON for non-effective style documents
    @NonNull public MapSource current;

    // Json constructor
    public StyleSource(String key, JsonNode sourceNode) {
        this.key = key;
        this.properties = new SourceProperties(sourceNode);
        this.current = new MapSource(key, sourceNode);
    }
}