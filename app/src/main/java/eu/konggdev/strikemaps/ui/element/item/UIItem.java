package eu.konggdev.strikemaps.ui.element.item;

import android.view.View;

import eu.konggdev.strikemaps.ui.UIComponent;

public interface UIItem {
    abstract View makeView(UIComponent spawner);

    default View.OnClickListener click(Runnable action) {
        return v -> action.run();
    }

    default View.OnLongClickListener longClick(Runnable action) { return v -> { action.run(); return true; };}
}
