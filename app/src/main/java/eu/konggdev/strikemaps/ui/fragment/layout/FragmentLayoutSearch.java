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
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

public class FragmentLayoutSearch extends Fragment implements Layout {
    AppController app;
    View rootView;

    private final Integer region;

    public FragmentLayoutSearch(AppController app, Integer region) {
        super(R.layout.fragment_search);
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
        //TODO: Make a floating menu instead of going right in settings
        setupButton(view, R.id.hamburgerButton, click(() -> app.getUi().swapScreen(DefinedScreen.SETTINGS)));
    }
}
