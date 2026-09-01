package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;

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


//        LinearLayout sourcesLayout = view.findViewById(R.id.llDownloadContainer);
//        for (MapSource source : sources)
//            sourcesLayout.addView(new InlineItem(source.name, () -> Toast.makeText(app.getActivity(), "Work in progress", Toast.LENGTH_SHORT).show()).makeView(app.getUi()));
    }
}