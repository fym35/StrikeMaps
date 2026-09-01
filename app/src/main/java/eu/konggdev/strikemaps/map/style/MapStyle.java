package eu.konggdev.strikemaps.map.style;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import eu.konggdev.strikemaps.map.style.document.StyleDocument;
import eu.konggdev.strikemaps.map.style.management.StyleManagementMetadata;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;

public class MapStyle {
    public static final class StoredRepresentation {
        public final String json;
        public final StyleOptions options;

        public final StyleManagementMetadata managementMetadata;

        public StoredRepresentation(String json, StyleOptions options, StyleManagementMetadata managementMetadata) {
            this.json = json;
            this.options = options;
            this.managementMetadata = managementMetadata;
        }

        public MapStyle restore() {
            MapStyle style = new MapStyle(
                    json,
                    options,
                    managementMetadata
            );

            return style;
        }
    }

    @NonNull public final StyleDocument document;
    @NonNull public final StyleOptions options;

    // Null when the style is not managed
    @Nullable public StyleManagementMetadata managementMetadata;

    // Original json representation of the style document, as we got it
    @NonNull public final String json;


    public MapStyle(@NonNull String json, @NonNull StyleOptions styleOptions, @Nullable StyleManagementMetadata managementMetadata) {
        this.json = json;
        this.document = new StyleDocument(json);
        this.options = styleOptions;
        this.managementMetadata = managementMetadata;
    }

    public MapStyle(@NonNull String json, @NonNull StyleDocument style, @NonNull StyleOptions styleOptions, @Nullable StyleManagementMetadata managementMetadata) {
        this.json = json;
        this.document = style;
        this.options = styleOptions;
        this.managementMetadata = managementMetadata;
    }

    public StoredRepresentation makeStoredRepresentation() {
         return new StoredRepresentation(json, options, managementMetadata);
    }

    public StyleDocument effectiveDocument() {
        return document.effectiveDocument(options);
    }
}
