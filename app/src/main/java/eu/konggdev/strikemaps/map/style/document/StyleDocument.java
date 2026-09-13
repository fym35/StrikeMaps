package eu.konggdev.strikemaps.map.style.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;
import eu.konggdev.strikemaps.map.style.source.StyleSource;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;

import java.util.ArrayList;
import java.util.List;

public class StyleDocument {
    //Only local data
    public String name;
    public String icon;

    public JsonNode metadata; // everything except layers + sources
    public List<StyleSource> sources;
    public ArrayNode layerDefinitions;  // "layers" array

    // Json constructor
    public StyleDocument(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);

            this.name = root.path("name").asText();
            this.icon = root.path("icon").asText();

            JsonNode jsonSources = root.path("sources");
            List<StyleSource> sources = new ArrayList<>();
            jsonSources.fields().forEachRemaining(entry -> {
                 sources.add(new StyleSource(entry.getKey(), entry.getValue()));
            });
            this.sources = sources;

            this.layerDefinitions = root.withArray("layers");

            ObjectNode metadata = root.deepCopy();
            metadata.remove("layers");
            metadata.remove("sources");
            this.metadata = metadata;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid style document", e);
        }
    }

    // Copy constructor
    public StyleDocument(StyleDocument other) {
        this.name = other.name;
        this.icon = other.icon;
        this.metadata = other.metadata.deepCopy();
        this.sources = new ArrayList<>(other.sources);
        this.layerDefinitions = other.layerDefinitions.deepCopy();
    }

    // The style that is presented to the renderer, with its options applied
    public StyleDocument effectiveDocument(StyleOptions options, RegistryStorageComponent registry) {
        StyleDocument result = new StyleDocument(this); //Copy
        for (int i = 0; i < sources.size(); i++) {
            StyleSource source = sources.get(i);
            MapSource effectiveSource = registry.getSource(
                    options.getInteger(source.key, 0)
            );
            if (effectiveSource != null) source.current = effectiveSource;
        }

        return result;
    }
}
