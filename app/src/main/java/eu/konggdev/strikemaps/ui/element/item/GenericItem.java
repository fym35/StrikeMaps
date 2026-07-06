package eu.konggdev.strikemaps.ui.element.item;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;

public class GenericItem implements UIItem {
    @NonNull public String name;
    public Bitmap image;
    public Runnable onClick;
    public Runnable onLongClick;
    boolean hasImage;

    public GenericItem(String refName) {
        this.name = refName;
        hasImage = false;
    }
    public GenericItem(String refName, Runnable onClick) {
        this.name = refName;
        this.onClick = onClick;
        hasImage = false;
    }
    public GenericItem(String refName, Runnable onClick, Runnable onLongClick) {
        this.name = refName;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        hasImage = false;
    }
    public GenericItem(String refName, Bitmap refImage) {
        this.name = refName;
        this.image = refImage;
        hasImage = true;
    }
    public GenericItem(String refName, Bitmap refImage, Runnable onClick) {
        this.name = refName;
        this.image = refImage;
        this.onClick = onClick;
        hasImage = true;
    }

    public GenericItem(String refName, Bitmap refImage, Runnable onClick, Runnable onLongClick) {
        this.name = refName;
        this.image = refImage;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        hasImage = true;
    }

    public final static GenericItem fromStyle(MapStyle style, MapComponent map, Runnable onClick) {
        if(style == null) return new GenericItem("Unknown");
        if(style.icon != null)
            return new GenericItem(style.name, style.icon, onClick);
        return new GenericItem(style.name, onClick);
    }
    public final static GenericItem fromStyle(MapStyle style, MapComponent map, Runnable onClick, Runnable onLongClick) {
        if(style == null) return new GenericItem("Unknown");
        if(style.icon != null)
            return new GenericItem(style.name, style.icon, onClick, onLongClick);
        return new GenericItem(style.name, onClick, onLongClick);
    }

    public View makeView(UIComponent spawner) {
        View v = spawner.inflateUi(R.layout.item_generic);
        //FIXME: These shouldn't be casted like that!
        ((TextView) v.findViewById(R.id.name)).setText(name);
        if(image != null) ((ImageButton) v.findViewById(R.id.image)).setImageBitmap(image);
        if(onClick != null) v.findViewById(R.id.image).setOnClickListener(click(onClick));
        if(onLongClick != null) v.findViewById(R.id.image).setOnLongClickListener(longClick(onLongClick));

        return v;
    }
}
