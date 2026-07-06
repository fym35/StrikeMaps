package eu.konggdev.strikemaps.ui.fragment.layout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.data.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.map.overlay.implementation.LocationOverlay;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;

public class FragmentLayoutControls extends Fragment implements Layout {
    AppController app;
    View rootView;

    private final Integer region;

    // Action definitions
    //*//
    public void notImplemented() { //Should never be called in release
        Toast.makeText(requireContext(), "Not implemented yet\nWait for release", Toast.LENGTH_SHORT).show();
    }

    public void toggleLocationService() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            app.getMap().switchOverlay(new LocationOverlay(app));
            setupView();
        }
    }

    public void zoomToLocation() {
        if(!app.getMap().hasOverlay(LocationOverlay.class)) {
            Toast.makeText(requireContext(), "Hold to enable location", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void attributtionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(app.getActivity())
                .setTitle(app.getActivity().getString(R.string.attribution_title))
                .setMessage(app.getActivity().getString(R.string.shipped_attribution))
                .setPositiveButton("OK", null).show();
        }
    //*//

    public FragmentLayoutControls(AppController app, Integer region) {
        super(R.layout.fragment_controls);
        this.app = app;
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
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;

        /* Restores location enabled status from user prefs,
           TODO: Should be moved out of UI code in the future */
        if(UserPrefsHelper.persistLocationEnabled(app.getPrefs()) && UserPrefsHelper.lastLocationEnabled(app.getPrefs()) && !app.getMap().hasOverlay(LocationOverlay.class))
            toggleLocationService();

        this.setupView();
    }

    public void setupView() {
        if (rootView == null) return;
        setupButton(rootView, R.id.layersButton, click(() -> app.getUi().getCurrentScreen().open(new FragmentMapChangePopup(app, R.id.bottomUi))));
        setupButton(rootView, R.id.attributionButton, click(this::attributtionDialog));
        setupButton(rootView, R.id.locationButton, click(this::zoomToLocation), longClick(this::toggleLocationService));

        //TODO
        setupButton(rootView, R.id.placesButton, click(this::notImplemented));
        setupButton(rootView, R.id.placesButton, click(this::notImplemented));
        setupButton(rootView, R.id.routeButton, click(this::notImplemented));
        setupButton(rootView, R.id.modeButton, click(this::notImplemented));

        TextView locationServiceStatusIndicator = rootView.findViewById(R.id.locationServiceStatusIndicator);
        if (app.getMap().hasOverlay(LocationOverlay.class)) {
            locationServiceStatusIndicator.setBackgroundColor(Color.parseColor("#00FF00")); //green
        } else {
            locationServiceStatusIndicator.setBackgroundColor(Color.parseColor("#FB0303")); //red
        }

        if(UserPrefsHelper.persistLocationEnabled(app.getPrefs()))
            UserPrefsHelper.lastLocationEnabled(app.getPrefs(), app.getMap().hasOverlay(LocationOverlay.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) toggleLocationService();
                else Toast.makeText(requireContext(), "You need to grant location permission", Toast.LENGTH_SHORT).show();
        }
    }
}
