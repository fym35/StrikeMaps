package eu.konggdev.strikemaps.ui.fragment.layout.content.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.helper.UserPrefsHelper;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;


public class FragmentLayoutContentSettings extends Fragment implements MainContentLayout {
    private final AppCompatActivity activity;
    private final UIComponent ui;

    private final SharedPreferences userPrefs;

    public FragmentLayoutContentSettings(AppCompatActivity activity, UIComponent ui, SharedPreferences userPrefs) {
        super(R.layout.fragment_settings);
        this.activity = activity;
        this.ui = ui;
        this.userPrefs = userPrefs;
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
        mapRendererSelector.setSelection(UserPrefsHelper.mapRenderer(userPrefs));
        final boolean[] ignoreFirst = {true};
        mapRendererSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (ignoreFirst[0]) {
                            ignoreFirst[0] = false;
                            return;
                        }

                        UserPrefsHelper.mapRenderer(userPrefs, position);
                        ui.alert(AlertDialogFactory.restartDialog(activity));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }
}
