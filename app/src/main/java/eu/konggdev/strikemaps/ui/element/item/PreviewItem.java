package eu.konggdev.strikemaps.ui.element.item;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import eu.konggdev.strikemaps.ui.UIComponent;
import org.maplibre.geojson.Feature;

import eu.konggdev.strikemaps.R;
public class PreviewItem implements UIItem {
    @NonNull public String name;
    public String details;
    public Bitmap image;
    boolean hasImage;
    public PreviewItem(String refName, String refType) {
        this.name = refName;
        this.details = refType;
        hasImage = false;
    }
    public PreviewItem(String refName, String refType, Bitmap refImage) {
        this.name = refName;
        this.details = refType;
        this.image = refImage;
        hasImage = true;
    }

    public static PreviewItem fromFeature(Feature feature) {
        return new PreviewItem(feature.getStringProperty("name"), feature.getStringProperty("class"));
    }

    @Override
    public View makeView(UIComponent spawner) {
        View view = spawner.inflateUi(R.layout.item_preview);
        ((TextView) view.findViewById(R.id.choiceName)).setText(name);
        if (details != null)
            ((TextView) view.findViewById(R.id.details)).setText(details);
        return view;
    }

    public View makeView(UIComponent spawner, View.OnClickListener onClick) {
        View view = makeView(spawner);
        view.setOnClickListener(onClick);
        return view;
    }
}
