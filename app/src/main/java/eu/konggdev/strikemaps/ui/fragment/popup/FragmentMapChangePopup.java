package eu.konggdev.strikemaps.ui.fragment.popup;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.LinearLayout;

import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;

import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.fragment.dialog.NewStyleBottomSheet;
import eu.konggdev.strikemaps.ui.fragment.dialog.StyleDetailsBottomSheet;
import eu.konggdev.strikemaps.ui.element.item.GenericItem;

import java.util.Map;

public class FragmentMapChangePopup extends Fragment implements Popup {
    @NonNull AppController app;
    @NonNull MapComponent map;
    @NonNull UIComponent ui;

    private final Integer region;

    private View view;

    Map<Integer, Integer> tabs =  Map.of(
            R.id.stylesTab, R.id.styles,
            R.id.optionsTab, R.id.options,
            R.id.overlaysTab, R.id.overlays
    );

    Map<Integer, Runnable> tabLoadActions = Map.of(
            R.id.stylesTab, this::reloadStyles,
            R.id.optionsTab, this::loadStyleOptions,
            R.id.overlaysTab, this::reloadOverlays
    );

    public void reloadStyles() {
        LinearLayout stylesLayout = view.findViewById(R.id.stylesLayout);
        stylesLayout.removeAllViews();
        app.getRegistry().getStyles().forEach((id, style) ->
                stylesLayout.addView(GenericItem.fromStyle(style.document, app,
                        () -> map.setStyle(style),
                        () -> this.styleDetails(style, id)).makeView(ui))
        );
        Bitmap addNewIcon = BitmapFactory.decodeResource(app.getActivity().getResources(), android.R.drawable.ic_menu_add);
        stylesLayout.addView(new GenericItem("",
                addNewIcon,
                this::newStyleFlow).makeView(ui));
    }

    public void loadStyleOptions() {
        LinearLayout optionsLayout = view.findViewById(R.id.optionsLayout);
//        optionsLayout.removeAllViews();
//        TextView sources = new AppCompatTextView(requireContext()) {{
//            setText("Sources");
//            setTextSize(16);
//            setLayoutParams(new ViewGroup.MarginLayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            ) {{ leftMargin = 16; }});
//        }};
//        optionsLayout.addView(sources);

//        for (MapSource source : map.style.sources) {
//            LinearLayout sourceSelectionLayout = new LinearLayout(requireContext());
//            TextView sourceName = new TextView(requireContext());
//            sourceName.setText(source.name);
//            sourceSelectionLayout.addView(sourceName);
//            optionsLayout.addView(sourceSelectionLayout);
//        }
    }

    public void reloadOverlays() {

    }

    void newStyleFlow() {
        new NewStyleBottomSheet(app, map, ui, this).show(app.getActivity().getSupportFragmentManager(), "NewStyleBottomSheet");
    }

    void styleDetails(MapStyle entry, Integer id) {
        new StyleDetailsBottomSheet(app, map, ui, this, entry, id).show(app.getActivity().getSupportFragmentManager(), "StyleDetailsBottomSheet");
    }

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

    @SuppressLint("UseCompatTextViewDrawableApis")
    void switchTab(int target) {
        tabs.forEach((button, content) -> {
            view.findViewById(content).setVisibility(button == target ? View.VISIBLE : View.GONE);
            int color = (button == target
                    ? Color.WHITE
                    : Color.parseColor("#888888"));
            ((TextView) view.findViewById(button)).setTextColor(color);
            ((TextView) view.findViewById(button)).setCompoundDrawableTintList(ColorStateList.valueOf(color));
        });
        tabLoadActions.get(target).run();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //FIXME
        this.view = view;

        tabs.keySet().forEach(button -> {
            view.findViewById(button).setOnClickListener(v -> switchTab(button));
        });

        switchTab(R.id.stylesTab); // Show styles
        setupButton(view, R.id.closeButton, click(() -> ui.getCurrentScreen().closePopup()));
        setupDragHandle(view, view, () -> ui.getCurrentScreen().closePopup());
    }
}
