package eu.konggdev.strikemaps.map.source.tiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SourceTiles {
    public enum TilesType {
        URL,
        TILES,
        DATA
    }

    private final TilesType type;
    private String url;
    private String[] tiles;

    private JsonNode data;

    public SourceTiles(String url) {
        this.url = url;
        this.type = TilesType.URL;
    }

    public SourceTiles(String[] tiles) {
        this.tiles = tiles;
        this.type = TilesType.TILES;
    }

    public SourceTiles(JsonNode data) {
        this.data = data;
        this.type = TilesType.DATA;
    }

    public void makeJson(ObjectMapper mapper, ObjectNode node) {
        switch (type) {
            case URL -> node.put("url", url);

            case TILES -> {
                ArrayNode tilesNode = mapper.createArrayNode();

                for (String tile : tiles)
                    tilesNode.add(tile);

                node.set("tiles", tilesNode);
            }

            case DATA -> node.set("data", data);
        }
    }
}
