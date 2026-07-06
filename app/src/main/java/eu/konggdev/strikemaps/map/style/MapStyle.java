package eu.konggdev.strikemaps.map.style;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.data.helper.FileHelper;
import eu.konggdev.strikemaps.map.source.MapSource;

import java.util.*;

public class MapStyle {
    //Only local data
    public String name;
    public Bitmap icon;

    public JsonNode metadata; // everything except layers + sources
    public Map<String, MapSource> sources;
    public ArrayNode layerDefinitions;  // "layers" array

    //FIXME
    public static MapStyle fromJsonFile(String filename, AppController app) {
        String styleContents;
        if (filename.startsWith("/storage")) styleContents = FileHelper.loadStringFromUserFile(filename);
        else styleContents = FileHelper.loadStringFromAssetFile(filename, app);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(styleContents);

            MapStyle style = new MapStyle();	
            style.name = root.path("name").asText();
            style.icon = getIcon(root.path("icon").asText(), app);

            style.sources = mapper.convertValue(
                    root.path("sources"),
                    new TypeReference<Map<String, MapSource>>() {}
            );

            style.layerDefinitions = root.withArray("layers");

            ObjectNode metadata = root.deepCopy();
            metadata.remove("layers");
            metadata.remove("sources");
            style.metadata = metadata;

            return style;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getIcon(String iconLocator, AppController app) {
        switch(iconLocator.split("//")[0]) {
            //TODO: https
            case "assets:":
                return BitmapFactory.decodeStream(FileHelper.openAssetStream("bundled/icon/" + iconLocator.split("//")[1], app));
            default:
                app.logcat("Unimplemented icon locator space: " + iconLocator);
                return null;
        }
    }
}
