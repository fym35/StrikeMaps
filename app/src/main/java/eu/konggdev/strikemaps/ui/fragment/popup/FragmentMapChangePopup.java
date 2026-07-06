package eu.konggdev.strikemaps.ui.fragment.popup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import eu.konggdev.strikemaps.ui.fragment.dialog.NewStyleBottomSheet;
import eu.konggdev.strikemaps.ui.fragment.dialog.StyleDetailsBottomSheet;
import eu.konggdev.strikemaps.ui.element.item.GenericItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FragmentMapChangePopup extends Fragment implements Popup {
    @NonNull AppController app;
    @NonNull MapComponent map;
    @NonNull UIComponent ui;

    private final Integer region;

    private View view;

    // Action definitions
    //*//
    void newStyleFlow() {
        new NewStyleBottomSheet(app, map, ui, this).show(app.getActivity().getSupportFragmentManager(), "NewStyleBottomSheet");
    }

    void styleDetails(MapStyle style, String stylePath) {
        new StyleDetailsBottomSheet(app, map, ui, this, style, stylePath).show(app.getActivity().getSupportFragmentManager(), "StyleDetailsBottomSheet");
    }
    //*//

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

    public void reloadStyles() {
        List<String> stylePaths = new ArrayList<>();
        stylePaths.addAll(Arrays.asList(FileHelper.getAssetFiles("bundled/style", ".style.json", app)));
        stylePaths.addAll(Arrays.asList(FileHelper.getUserFiles("style", ".style.json", app)));
        LinearLayout stylesLayout = view.findViewById(R.id.stylesLayout);
        stylesLayout.removeAllViews();
        for (String stylePath : stylePaths) {
            MapStyle parsedStyle = MapStyle.fromFile(stylePath, app);
            stylesLayout.addView(GenericItem.fromStyle(parsedStyle, map,
                    () -> map.setStyle(parsedStyle),
                    () -> this.styleDetails(parsedStyle, stylePath)).makeView(ui));
        }
        Bitmap addNewIcon = BitmapFactory.decodeResource(app.getActivity().getResources(), android.R.drawable.ic_menu_add);
        stylesLayout.addView(new GenericItem("",
                addNewIcon,
                this::newStyleFlow).makeView(ui));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //FIXME
        setupButton(view, R.id.closeButton, click(() -> ui.getCurrentScreen().closePopup()));
        setupDragHandle(view, view, () -> ui.getCurrentScreen().closePopup());
        this.view = view;
        reloadStyles();
    }
}
