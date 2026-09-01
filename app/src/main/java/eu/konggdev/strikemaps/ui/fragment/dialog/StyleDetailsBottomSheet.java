package eu.konggdev.strikemaps.ui.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;

import java.io.IOException;
import java.io.OutputStream;

public class StyleDetailsBottomSheet extends BottomSheetDialogFragment {
    @NonNull
    AppController app;
    @NonNull
    MapComponent map;
    @NonNull
    UIComponent ui;
    @NonNull
    final FragmentMapChangePopup mapChangePopup;

    private final MapStyle style;
    private final Integer id;

    private ActivityResultLauncher<Intent> exportLauncher;

    void deleteStyle() {
        app.getRegistry().deleteStyle(id);

        mapChangePopup.reloadStyles();
        dismiss();
    }

    private void showExportDialog() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");

        exportLauncher.launch(intent);
    }

    public StyleDetailsBottomSheet(AppController app, MapComponent map, UIComponent ui, FragmentMapChangePopup mapChangePopup, MapStyle style, Integer id) {
        this.app = app;
        this.map = map;
        this.ui = ui;
        this.mapChangePopup = mapChangePopup;
        this.style = style;
        this.id = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK
                            && result.getData() != null) {

                        Uri uri = result.getData().getData();

                        try (OutputStream out =
                                     requireContext()
                                             .getContentResolver()
                                             .openOutputStream(uri)) {

                            if (out != null) {
                                out.write(style.json.getBytes());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_style_details, container, false);

        TextView styleNameView = view.findViewById(R.id.styleName);
        TextView styleTypeView = view.findViewById(R.id.styleType);

        TextView builtInStyleAlert = view.findViewById(R.id.builtInStyleAlert);
        MaterialCardView editButtonLayout = view.findViewById(R.id.editButton);
        MaterialCardView copyButtonLayout = view.findViewById(R.id.copyButton);
        MaterialCardView exportButtonLayout = view.findViewById(R.id.exportButton);
        MaterialCardView deleteButtonLayout = view.findViewById(R.id.deleteButton);
        MaterialCardView closeButtonLayout = view.findViewById(R.id.closeButton);

        styleNameView.setText(style.document.name);

        if (style.managementMetadata != null) {
            styleTypeView.setText("Built-In Style");
        } else {
            styleTypeView.setText("User Style");
        }

        editButtonLayout.setOnClickListener(v -> Toast.makeText(requireContext(), "Editor not implemented yet\nWait for release", Toast.LENGTH_SHORT).show());
        copyButtonLayout.setOnClickListener(v -> ui.alert(AlertDialogFactory.createStyle(app, style.json, mapChangePopup)));
        exportButtonLayout.setOnClickListener(v -> showExportDialog());
        deleteButtonLayout.setOnClickListener(v -> deleteStyle());
        closeButtonLayout.setOnClickListener(v -> dismiss());

        return view;
    }
}
