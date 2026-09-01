package eu.konggdev.strikemaps.map.overlay.implementation;

import android.location.Location;
import android.location.LocationListener;
import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.JsonNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.overlay.MapOverlay;

import eu.konggdev.strikemaps.data.provider.LocationDataProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class LocationOverlay implements MapOverlay, LocationListener {
    LocationDataProvider locationDataProvider;
    AppController app;
    MapComponent map;

    public Location currentLocation = null;

    public LocationOverlay(AppController app) {
        this.app = app;
        this.map = app.getMap();
        this.locationDataProvider = new LocationDataProvider(app.getActivity(), this);
    }

	@Override
	public JsonNode makePatch() {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode root = mapper.createObjectNode();

		ObjectNode sources = mapper.createObjectNode();
		ObjectNode location = mapper.createObjectNode();
		ObjectNode data = mapper.createObjectNode();
		ObjectNode geometry = mapper.createObjectNode();
		ObjectNode properties = mapper.createObjectNode();

		ArrayNode coordinates = mapper.createArrayNode();
		coordinates.add(currentLocation.getLongitude());
		coordinates.add(currentLocation.getLatitude());

		geometry.put("type", "Point");
		geometry.set("coordinates", coordinates);

		data.put("type", "Feature");
		data.set("geometry", geometry);
		data.set("properties", properties);

		location.put("type", "geojson");
		location.set("data", data);

		sources.set("location", location);
		root.set("sources", sources);

		// layers
		ArrayNode layers = mapper.createArrayNode();
		ObjectNode layer = mapper.createObjectNode();

		layer.put("id", "location");
		layer.put("type", "circle");
		layer.put("source", "location");

		ObjectNode paint = mapper.createObjectNode();
		paint.put("circle-radius", 5);
		paint.put("circle-color", "#1E88E5");

		paint.put("circle-stroke-color", "#FFFFFF");
		paint.put("circle-stroke-width", 1.5);

		layer.set("paint", paint);

		layers.add(layer);
		root.set("layers", layers);

		return root;
	}

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.currentLocation = location;
        map.overlayUpdate(this);
    }
}
