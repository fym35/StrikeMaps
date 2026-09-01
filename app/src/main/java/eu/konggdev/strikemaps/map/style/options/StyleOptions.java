package eu.konggdev.strikemaps.map.style.options;

import eu.konggdev.strikemaps.map.source.MapSource;

import java.util.HashMap;
import java.util.Map;

public class StyleOptions {
    private Map<String, Object> values;

    public StyleOptions() {
        this.values = new HashMap<>();
    }

    public StyleOptions(Map<String, Object> values) {
        this.values = values;
    }

    public boolean getBoolean(String id, boolean defaultValue) {
        Object value = values.get(id);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public int getInteger(String id, int defaultValue) {
        Object value = values.get(id);
        return value instanceof Number
                ? ((Number) value).intValue()
                : defaultValue;
    }
}
