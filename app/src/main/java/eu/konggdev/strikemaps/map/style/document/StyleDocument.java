package eu.konggdev.strikemaps.map.style.document;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.helper.FileHelper;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;

import java.util.ArrayList;
import java.util.List;

public class StyleDocument {

    //Only local data
    public String name;
    public String icon;

    public JsonNode metadata; // everything except layers + sources
    public List<MapSource> sources;
    public ArrayNode layerDefinitions;  // "layers" array

    // Json constructor
    public StyleDocument(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);

            this.name = root.path("name").asText();
            this.icon = root.path("icon").asText();

            JsonNode jsonSources = root.path("sources");
            List<MapSource> sources = new ArrayList<>();
            jsonSources.fields().forEachRemaining(entry -> {
                sources.add(MapSource.fromJson(MapSource.MapSourceContractType.REQUEST, entry.getKey(), entry.getValue()));
            });
            this.sources = sources;

            this.layerDefinitions = root.withArray("layers");

            ObjectNode metadata = root.deepCopy();
            metadata.remove("layers");
            metadata.remove("sources");
            this.metadata = metadata;

        } catch (Exception e) {
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
    public StyleDocument effectiveDocument(StyleOptions options) {
        StyleDocument result = new StyleDocument(this); //Copy
        for (JsonNode layer : result.layerDefinitions) {
            JsonNode option = layer.get("option");

            if (option == null)
                continue;

            if ("enable".equals(option.path("type").asText())) {
                String id = layer.path("id").asText();

                boolean enabled = options.getBoolean(
                         id,
                        option.path("default").asBoolean(true)
                );


            }
        }

        return result;
    }
}
