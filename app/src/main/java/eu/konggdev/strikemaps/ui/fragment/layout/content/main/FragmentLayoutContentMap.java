package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.R;

public class FragmentLayoutContentMap extends Fragment implements MainContentLayout {
    View mapView;

    public FragmentLayoutContentMap(View refMapView) {
        super(R.layout.fragment_map);
        this.mapView = refMapView;
    }

    @Override
    public Fragment toFragment() {
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout layout = (LinearLayout) view;
        if(mapView.getParent() != null) ((ViewGroup) mapView.getParent()).removeView(mapView);
        layout.addView(mapView);
    }
}
