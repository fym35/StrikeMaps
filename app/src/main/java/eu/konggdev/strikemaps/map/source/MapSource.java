package eu.konggdev.strikemaps.map.source;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.map.source.tiles.SourceTiles;

public class MapSource {
    public String name;

    public SourceTiles tileSource;

    public String type;
    public String schema;

    public int minzoom;
    public int maxzoom;

    public String scheme;
    public int tileSize;

    public String encoding;

    public MapSource(String name, SourceTiles tileSource, String type, String schema) {
        this.name = name;
        this.tileSource = tileSource;
        this.type = type;
        this.schema = schema;
    }

    public MapSource(String key, JsonNode sourceNode) {
        this.name = key;

        this.type = sourceNode.path("type").asText(null);
        this.schema = sourceNode.path("schema").asText(null);
        this.scheme = sourceNode.path("scheme").asText(null);
        this.encoding = sourceNode.path("encoding").asText(null);

        this.minzoom = sourceNode.path("minZoom").asInt(0);
        this.maxzoom = sourceNode.path("maxZoom").asInt(24);
        this.tileSize = sourceNode.path("tileSize").asInt(256);

        this.tileSource = handleJsonTileSource(sourceNode);
    }

    private static SourceTiles handleJsonTileSource(JsonNode sourceNode) {
        // By design, a source must use either "url" or "tiles", never both
        // In case both are present, we prefer URL over tiles... because I don't know, we just do, m'kay?
        if (sourceNode.has("url"))
            return new SourceTiles(sourceNode.get("url").asText());

        if (sourceNode.has("tiles")) {
            String[] tiles = new String[0];
            ObjectMapper mapper = new ObjectMapper();

            try {
                tiles = mapper.treeToValue(sourceNode.get("tiles"), String[].class);
            } catch (Exception e) { // If we can't parse it, lets just keep it an empty array
                e.printStackTrace();
            }

            return new SourceTiles(tiles);
        }


        //TODO: Decide what to do when we have a definition that doesn't define the most important part - the tile source
        //Maybe throwing some custom exception and propagating it back to the user would be appropriate
        return null;
    }

    public ObjectNode makeJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        try {
            if (type != null) node.put("type", type);
            if (schema != null) node.put("schema", schema);
            if (minzoom != 0) node.put("minzoom", minzoom);
            if (maxzoom != 24) node.put("maxzoom", maxzoom);
            if (scheme != null) node.put("scheme", scheme);
            if (tileSize != 256) node.put("tileSize", tileSize);
            if (encoding != null) node.put("encoding", encoding);
            if (tileSource != null) tileSource.makeJson(mapper, node);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return node;
    }
}
