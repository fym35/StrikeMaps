package eu.konggdev.strikemaps.map.source.properties;

import com.fasterxml.jackson.databind.JsonNode;

public class SourceProperties {
    public String type;
    public String schema;

    // Json constructor
    public SourceProperties(JsonNode sourceNode) {
        this.type = sourceNode.path("type").asText(null);
        this.schema = sourceNode.path("schema").asText(null);
    }
}
