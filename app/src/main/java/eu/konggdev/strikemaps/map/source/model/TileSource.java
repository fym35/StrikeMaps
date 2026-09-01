package eu.konggdev.strikemaps.map.source.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TileSource {
    public enum TileSourceType {
        URL,
        TILES,
        DATA
    }
    private TileSourceType type;
    private String url;
    private String[] tiles;

    private JsonNode data;

    public TileSource(String url) {
        this.url = url;
        this.type = TileSourceType.URL;
    }

    public TileSource(String[] tiles) {
        this.tiles = tiles;
        this.type = TileSourceType.TILES;
    }

    public TileSource(JsonNode data) {
        this.data = data;
        this.type = TileSourceType.DATA;
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
