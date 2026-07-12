package eu.konggdev.strikemaps.ui.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.data.helper.FileHelper;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.media.MediaExtractor.MetricsConstants.MIME_TYPE;

public class StyleDetailsBottomSheet extends BottomSheetDialogFragment {
    @NonNull
    AppController app;
    @NonNull
    MapComponent map;
    @NonNull
    UIComponent ui;
    @NonNull
    FragmentMapChangePopup mapChangePopup;

    private final MapStyle style;
    private final String stylePath;

    private final ActivityResultLauncher<Intent> exportLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == android.app.Activity.RESULT_OK
                                && result.getData() != null) {

                            Uri uri = result.getData().getData();

                            try (OutputStream out =
                                         requireContext()
                                                 .getContentResolver()
                                                 .openOutputStream(uri)) {

                                out.write(getContents().getBytes());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

    //Action definitions
    //*//
    void deleteStyle() {
        boolean deleted = FileHelper.deleteFile(stylePath);
        if (!deleted) {
            Toast.makeText(requireContext(), "Failed deleting style", Toast.LENGTH_SHORT).show();
            return;
        }

        mapChangePopup.reloadStyles();
        dismiss();
    }


    private void showExportDialog(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        exportLauncher.launch(intent);
    }
    //*//

    String getContents() {
        if (stylePath.startsWith("/storage")) return FileHelper.loadStringFromUserFile(stylePath);
        else return FileHelper.loadStringFromAssetFile(stylePath, app);
    }

    public StyleDetailsBottomSheet(AppController app, MapComponent map, UIComponent ui, FragmentMapChangePopup mapChangePopup, MapStyle style, String stylePath) {
        this.app = app;
        this.map = map;
        this.ui = ui;
        this.mapChangePopup = mapChangePopup;
        this.style = style;
        this.stylePath = stylePath;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_style_details, container, false);

        TextView styleNameView = view.findViewById(R.id.styleName);
        TextView fileNameView = view.findViewById(R.id.fileName);
        TextView styleTypeView = view.findViewById(R.id.styleType);

        TextView builtInStyleAlert = view.findViewById(R.id.builtInStyleAlert);
        ConstraintLayout editButtonLayout = view.findViewById(R.id.editButton);
        ConstraintLayout copyButtonLayout = view.findViewById(R.id.copyButton);
        ConstraintLayout exportButtonLayout = view.findViewById(R.id.exportButton);
        ConstraintLayout deleteButtonLayout = view.findViewById(R.id.deleteButton);
        ConstraintLayout closeButtonLayout = view.findViewById(R.id.closeButton);


        styleNameView.setText(style.name);

        String[] pathSplit = stylePath.split("/");
        String fileName = pathSplit[pathSplit.length - 1];

        fileNameView.setText(fileName);

        if (Objects.equals(pathSplit[0], "bundled") && pathSplit.length > 1) {
            styleTypeView.setText("Built-In Style");
            editButtonLayout.setVisibility(View.GONE);
            builtInStyleAlert.setVisibility(View.VISIBLE);
            deleteButtonLayout.setVisibility(View.GONE);
        } else {
            styleTypeView.setText("User Style");
        }

        editButtonLayout.setOnClickListener(v ->  Toast.makeText(requireContext(), "Editor not implemented yet\nWait for release", Toast.LENGTH_SHORT).show());
        copyButtonLayout.setOnClickListener(v -> ui.alert(AlertDialogFactory.createStyle(app, getContents(), mapChangePopup)));
        exportButtonLayout.setOnClickListener(v -> showExportDialog(fileName));
        deleteButtonLayout.setOnClickListener(v -> deleteStyle());
        closeButtonLayout.setOnClickListener(v -> dismiss());

        return view;
    }
}
