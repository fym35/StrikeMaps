package eu.konggdev.strikemaps.ui.element.item;

import android.view.View;
import android.widget.TextView;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.ui.UIComponent;

public class InlineItem implements UIItem {
    public String text;
    public Runnable onClick;

    public InlineItem(String refText) {
        this.text = refText;
    }

    public InlineItem(String refText, Runnable onClick) {
        this.text = refText;
        this.onClick = onClick;
    }

    public static InlineItem fromSource(MapSource source) {
        return new InlineItem(source.name);
    }

    public static InlineItem fromSource(MapSource source, Runnable onClick) {
        return new InlineItem(source.name, onClick);
    }


    @Override
    public View makeView(UIComponent spawner) {
        View view = spawner.inflateUi(R.layout.item_inline);
        ((TextView) view.findViewById(R.id.inline)).setText(text);
        if(onClick != null) view.findViewById(R.id.inline).setOnClickListener(click(onClick));
        return view;
    }
}
