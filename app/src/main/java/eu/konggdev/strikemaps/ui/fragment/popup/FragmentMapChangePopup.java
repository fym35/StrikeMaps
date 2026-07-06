package eu.konggdev.strikemaps.ui.fragment.popup;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.LinearLayout;

import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.data.helper.FileHelper;
import eu.konggdev.strikemaps.map.MapComponent;

import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.element.item.GenericItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FragmentMapChangePopup extends Fragment implements Popup {
    @NonNull AppController app;
    @NonNull MapComponent map;
    @NonNull UIComponent ui;

    private final Integer region;

    public FragmentMapChangePopup(AppController app, Integer region) {
        super(R.layout.popup_map_change);
        this.app = app;
        this.map = app.getMap();
        this.ui = app.getUi();
        this.region = region;
    }

    @Override
    public Integer getRegion() {
        return region;
    }

    @Override
    public Fragment toFragment() {
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //FIXME
        setupButton(view, R.id.closeButton, click(() -> ui.getCurrentScreen().closePopup()));
        setupDragHandle(view, view, () -> ui.getCurrentScreen().closePopup());
        List<String> stylePaths = new ArrayList<>();
        stylePaths.addAll(Arrays.asList(FileHelper.getAssetFiles("bundled/style", ".style.json", app)));
        stylePaths.addAll(Arrays.asList(FileHelper.getUserFiles("style", ".style.json", app)));
        LinearLayout stylesLayout = view.findViewById(R.id.stylesLayout);
        for (String style : stylePaths)
            stylesLayout.addView(GenericItem.fromStyle(MapStyle.fromJsonFile(style, app), map).makeView(ui));
    }
}
