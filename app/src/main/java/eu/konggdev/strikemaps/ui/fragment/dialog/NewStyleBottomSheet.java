package eu.konggdev.strikemaps.ui.fragment.dialog;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;

import java.io.*;

public class NewStyleBottomSheet extends BottomSheetDialogFragment {
    private final String styleBase = "{\"name\":\"None\"}";

    private AppCompatActivity activity;

    private MapComponent map;

    private UIComponent ui;

    private RegistryStorageComponent registry;

    private FragmentMapChangePopup mapChangePopup;

    private final ActivityResultLauncher<Intent> importLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK
                                && result.getData() != null) {

                            Uri uri = result.getData().getData();

                            try (InputStream in = requireContext()
                                    .getContentResolver()
                                    .openInputStream(uri)) {

                                try (Cursor cursor = requireContext()
                                        .getContentResolver()
                                        .query(uri, null, null, null, null)) {

                                    if (cursor != null && cursor.moveToFirst()) {
                                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                        if (nameIndex >= 0) {
                                            String fileName = cursor.getString(nameIndex);

                                            BufferedReader reader = new BufferedReader(
                                                    new InputStreamReader(in)
                                            );

                                            StringBuilder contentBuilder = new StringBuilder();
                                            String line;

                                            while ((line = reader.readLine()) != null) {
                                                contentBuilder.append(line).append(System.lineSeparator());
                                            }
                                            reader.close();
                                            String content = contentBuilder.toString();

                                            if (fileName != null && !fileName.endsWith(".style.json")) {
                                                ui.alert(
                                                        AlertDialogFactory.createStyle(
                                                                activity,
                                                                registry,
                                                                content,
                                                                mapChangePopup
                                                        )
                                                );
                                            } else {
                                                registry.addStyle(
                                                        new MapStyle(
                                                                content,
                                                                new StyleOptions(),
                                                                null
                                                        )
                                                );
                                                mapChangePopup.reloadStyles();
                                            }
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

    private void showImportDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        importLauncher.launch(intent);
    }

    public NewStyleBottomSheet(AppCompatActivity activity, MapComponent map, UIComponent ui, RegistryStorageComponent registry, FragmentMapChangePopup mapChangePopup) {
        this.activity = activity;
        this.map = map;
        this.ui = ui;
        this.registry = registry;
        this.mapChangePopup = mapChangePopup;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_new_style, container, false);

        MaterialCardView builtInBtn = view.findViewById(R.id.buttonFromBuiltInStyle);
        MaterialCardView fileBtn = view.findViewById(R.id.buttonFromFile);
        MaterialCardView emptyBtn = view.findViewById(R.id.buttonCreateEmpty);

        builtInBtn.setOnClickListener(v -> {
            ui.alert(AlertDialogFactory.copyBuiltInStyle(activity, map, ui, mapChangePopup));
        });

        fileBtn.setOnClickListener(v -> {
            showImportDialog();
        });

        emptyBtn.setOnClickListener(v -> {
            ui.alert(AlertDialogFactory.createStyle(activity, registry, styleBase, mapChangePopup));
        });

        return view;
    }
}