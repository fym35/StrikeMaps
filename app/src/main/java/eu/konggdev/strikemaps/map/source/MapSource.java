package eu.konggdev.strikemaps.map.source;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.map.source.model.TileSource;

public class MapSource {
    public enum MapSourceContractType {
        REQUEST,
        DEFINITION
    }

    /*
     * Contract describes the "purpose" of the source.
     *
     * This exists because we effectively have two types
     * of sources:
     *   - Sources that are the style asking for a specific type of source
     *     and defining a fallback, which is also used as a default for
     *     
     * and
     *   - Sources that are the actual source
     */
    @NonNull
    public final MapSourceContractType contract;

    /*
     * For a request contract:
     *  name is the internal key used by the style.
     *
     * For a definition contract:
     *   name is the user-facing source name
     *   (e.g. "ArcGIS Imagery").
     *
     * When converting a request into a definition
     * (when importing a style whose requests
     * cannot be satisfied), we look for a name
     * field and fall back to normalizing the key instead
     */
    public String name;

    /*
     * For a request contract, this is the fallback
     * for when we absolutely cannot satisfy the requirement,
     * or the defaults for converting into a definition contract.
     */
    public TileSource tileSource;

    public String type;
    public String schema;

    public int minzoom;
    public int maxzoom;

    public String scheme;
    public int tileSize;

    public String encoding;

    public MapSource(@NonNull MapSourceContractType contract, String name, TileSource tileSource, String type, String schema) {
        this.contract = contract;
        this.name = name;
        this.tileSource = tileSource;
        this.type = type;
        this.schema = schema;
    }

    private MapSource(@NonNull MapSourceContractType contract) {
        this.contract = contract;
    }

    public static MapSource fromJson(MapSourceContractType contract, String key, JsonNode sourceNode) {
        MapSource result = new MapSource(contract);
        result.name = key;

        result.schema = sourceNode.path("schema").asText(null);
        result.scheme = sourceNode.path("scheme").asText(null);
        result.encoding = sourceNode.path("encoding").asText(null);
        result.type = sourceNode.path("type").asText(null);

        result.minzoom = sourceNode.path("minZoom").asInt(0);
        result.maxzoom = sourceNode.path("maxZoom").asInt(24);
        result.tileSize = sourceNode.path("tileSize").asInt(256);

        result.tileSource = handleJsonTileSource(contract, sourceNode);

        return result;
    }

    private static TileSource handleJsonTileSource(MapSourceContractType contract, JsonNode sourceNode) {
        // By design, a source must use either "url" or "tiles", never both
        // In case both are present, we prefer URL over tiles... because I don't know, we just do, m'kay?
        if (sourceNode.has("url"))
            return new TileSource(sourceNode.get("url").asText());

        if (sourceNode.has("tiles")) {
            String[] tiles = new String[0];
            ObjectMapper mapper = new ObjectMapper();

            try {
                tiles = mapper.treeToValue(sourceNode.get("tiles"), String[].class);
            } catch (Exception e) { // If we can't parse it, lets just keep it an empty array
                e.printStackTrace();
            }

            return new TileSource(tiles);
        }

        if (contract == MapSourceContractType.REQUEST) //No tile source is only acceptable for a request contract
            //TODO: Define an empty tile source?
            return null;

        //TODO: Decide what to do when we have a definition that doesn't define the most important part - the tile source
        //Maybe throwing some custom exception, catching it in fromJson calls and propagating it back to the user would be appropriate
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
