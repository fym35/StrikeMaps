package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.data.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.app.AppController;

import java.util.Arrays;


public class FragmentLayoutContentSettings extends Fragment implements MainContentLayout {
    @NonNull AppController app;

    public FragmentLayoutContentSettings(AppController app) {
        super(R.layout.fragment_settings);
        this.app = app;
    }

    @Override
    public Fragment toFragment() {
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Spinner mapRendererSelector = view.findViewById(R.id.mapRendererSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.map_renderers,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapRendererSelector.setAdapter(adapter);
        mapRendererSelector.setSelection(UserPrefsHelper.mapRenderer(app.getPrefs()));
        mapRendererSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UserPrefsHelper.mapRenderer(app.getPrefs(), position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }
}
