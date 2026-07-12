package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.data.helper.FileHelper;
import eu.konggdev.strikemaps.map.source.MapSource;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.element.item.GenericItem;
import eu.konggdev.strikemaps.ui.element.item.PreviewItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FragmentLayoutContentOfflineMaps extends Fragment implements MainContentLayout {
    @NonNull AppController app;

    public FragmentLayoutContentOfflineMaps(AppController app) {
        super(R.layout.fragment_offline_maps);
        this.app = app;
    }

    @Override
    public Fragment toFragment() {
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<MapSource> sources = new ArrayList<>();

        List<String> stylePaths = new ArrayList<>();
        stylePaths.addAll(Arrays.asList(FileHelper.getAssetFiles("bundled/style", ".style.json", app)));
        stylePaths.addAll(Arrays.asList(FileHelper.getUserFiles("style", ".style.json", app)));
        /* Parsing an entire MapStyle is absolutely unnecessary and resource wasteful
           TODO: A method should be implemented to parse a List<MapSource> directly from the JSON */
        for (String stylePath : stylePaths) {
            MapStyle parsedStyle = MapStyle.fromFile(stylePath, app);
            sources.addAll(parsedStyle.sources.values());
        }

        LinearLayout sourcesLayout = view.findViewById(R.id.llDownloadContainer);
        for (MapSource source : sources) {
            if (!Objects.equals(source.type, "raster"))
                sourcesLayout.addView(new PreviewItem(source.url, "").makeView(app.getUi()));
            else
                sourcesLayout.addView(new PreviewItem(source.tiles.toString(), "").makeView(app.getUi()));
        }
    }
}