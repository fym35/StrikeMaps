package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.R;

public class FragmentLayoutContentSettings extends Fragment implements MainContentLayout {
    public FragmentLayoutContentSettings() {
        super(R.layout.fragment_settings);
    }

    @Override
    public Fragment toFragment() {
        return this;
    }
}
