package eu.konggdev.strikemaps.ui.element.item;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.map.style.document.StyleDocument;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.util.icon.IconResolver;

public class GenericItem implements UIItem {
    public String name;

    public Bitmap image;
    public Runnable onClick;
    public Runnable onLongClick;
    boolean hasImage;

    public GenericItem(String name) {
        this.name = name;
        hasImage = false;
    }

    public GenericItem(String name, Runnable onClick) {
        this.name = name;
        this.onClick = onClick;
        hasImage = false;
    }

    public GenericItem(String name, Runnable onClick, Runnable onLongClick) {
        this.name = name;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        hasImage = false;
    }

    public GenericItem(String name, Bitmap Image) {
        this.name = name;
        this.image = Image;
        hasImage = true;
    }

    public GenericItem(String name, Bitmap Image, Runnable onClick) {
        this.name = name;
        this.image = Image;
        this.onClick = onClick;
        hasImage = true;
    }

    public GenericItem(String name, Bitmap Image, Runnable onClick, Runnable onLongClick) {
        this.name = name;
        this.image = Image;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        hasImage = true;
    }

    public GenericItem(StyleDocument style, Runnable onClick, AppCompatActivity activity) {
        if (style == null) return;
        this.name = style.name;
        this.image = style.icon != null ? IconResolver.getIcon(style.icon, activity) : null;
        this.onClick = onClick;
    }

    public GenericItem(StyleDocument style, Runnable onClick, Runnable onLongClick, AppCompatActivity activity) {
        if (style == null) return;
        this.name = style.name;
        this.image = style.icon != null ? IconResolver.getIcon(style.icon, activity) : null;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
    }

    @Override
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
