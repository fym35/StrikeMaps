package eu.konggdev.strikemaps.ui.factory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.app.util.JsonPatcher;
import eu.konggdev.strikemaps.data.helper.FileHelper;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.element.item.GenericItem;
import eu.konggdev.strikemaps.ui.element.item.PreviewItem;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;
import org.maplibre.geojson.Feature;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static androidx.core.content.ContextCompat.getSystemService;

//FIXME: Move Item functions into specific classes for specific types - e.g. StyleItem
public final class AlertDialogFactory {
    public static AlertDialog copyBuiltInStyle(AppController app, MapComponent map, UIComponent ui, FragmentMapChangePopup mapChangePopup) {
        //TODO: Use an UI element thats supposed to be vertical, instead of GenericItem
        List<String> styles = Arrays.asList(FileHelper.getAssetFiles("bundled/style", ".style.json", app));

        LinearLayout container = new LinearLayout(app.getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

        for (String style : styles) {
            View itemView = GenericItem
                    .fromStyle(
                            MapStyle.fromFile(style, app),
                            map,
                            () -> ui.alert(AlertDialogFactory.createStyle(app, FileHelper.loadStringFromAssetFile(style, app), mapChangePopup))
                    )
                    .makeView(ui);

            container.addView(itemView);
        }

        ScrollView scrollView = new ScrollView(app.getActivity());
        scrollView.addView(container);

        AlertDialog dialog = new AlertDialog.Builder(app.getActivity())
                .setTitle("Copy from")
                .setView(scrollView)
                .setNegativeButton("Cancel", null)
                .create();

        return dialog;
    }

    public static AlertDialog createStyle(AppController app, String baseStyleContents, FragmentMapChangePopup mapChangePopup) {
        final EditText nameInput = new EditText(app.getActivity());
        nameInput.setHint("Name");

        final CheckBox inferFileName = new CheckBox(app.getActivity());
        inferFileName.setText("Infer filename automatically");
        inferFileName.setChecked(true);

        final EditText fileInput = new EditText(app.getActivity());
        fileInput.setHint("Filename");

        final TextView inferedFileName = new TextView(app.getActivity());
        inferedFileName.setPadding(0, 2, 0, 0);
        LinearLayout container = new LinearLayout(app.getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * app.getActivity().getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        container.addView(nameInput);
        container.addView(inferFileName);
        container.addView(fileInput);
        container.addView(inferedFileName);

        inferFileName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fileInput.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            inferedFileName.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        fileInput.setVisibility(inferFileName.isChecked() ? View.GONE : View.VISIBLE);
        inferedFileName.setVisibility(inferFileName.isChecked() ? View.VISIBLE : View.GONE);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameText = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    if (!s.isEmpty() && s != null) {
                        nameText = "File name will be: " + s.toString().toLowerCase(Locale.ROOT) + ".style.json";
                    }
                } else if (s != null) {
                    nameText = "File name will be: " + s.toString().toLowerCase(Locale.ROOT) + ".style.json";
                }

                inferedFileName.setText(nameText);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        AlertDialog dialog = new AlertDialog.Builder(app.getActivity())
                .setTitle("Create")
                .setView(container)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button createButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            createButton.setOnClickListener(v -> {
                ObjectMapper mapper = new ObjectMapper();
                boolean nameEntryRequired = true;
                String styleContentName = "";
                if (baseStyleContents != null) {
                    try {
                        JsonNode root = mapper.readTree(baseStyleContents);
                        if (!root.path("name").asText().isEmpty()) {
                            nameEntryRequired = false; //We can take the name from the style
                            styleContentName = root.path("name").asText();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String name = nameInput.getText().toString().trim();

                if (name.isEmpty() && nameEntryRequired) {
                    nameInput.setError("Name required");
                    return;
                } else if (name.isEmpty() && !nameEntryRequired) {
                    name = styleContentName;
                }

                assert !name.isEmpty();

                String fileName;
                if (inferFileName.isChecked()) {
                    fileName = name.toLowerCase(Locale.ROOT) + ".style.json";
                } else {
                    fileName = fileInput.getText().toString();
                    if (fileName.isEmpty()) {
                        fileInput.setError("File name required");
                        return;
                    }

                    if (!fileName.endsWith(".style.json")) {
                        fileInput.setError("File must end with .style.json");
                        return;
                    }
                }

                try {
                    JsonNode root;
                    if(baseStyleContents != null) {
                        if(!baseStyleContents.isEmpty()) {
                            root = mapper.readTree(baseStyleContents);
                        } else {
                            root = mapper.createObjectNode();
                        }
                    } else {
                        root = mapper.createObjectNode();
                    }

                    if (!root.path("name").asText().isEmpty()) {
                        ObjectNode node = mapper.createObjectNode();
                        node.put("name", name);
                        root = JsonPatcher.patch(root, node);
                    }

                        if (FileHelper.userFileExists("style", fileName, app)) {
                            app.getUi().alert(askUserOverwriteFile(app, fileName, "style", mapper.writeValueAsString(root), dialog, mapChangePopup));
                        } else {
                            FileHelper.writeUserFile("style", fileName, mapper.writeValueAsString(root), app);
                        }
                        dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(app.getActivity(), "Failed to create", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                mapChangePopup.reloadStyles();
            });
        });

        return dialog;
    }

    public static AlertDialog askUserOverwriteFile(AppController app, String fileName, String path, String content, FragmentMapChangePopup mapChangePopup) {
        return new AlertDialog.Builder(app.getActivity())
                .setMessage("Style of filename " + fileName + " already exists, do you wish to overwrite it?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        FileHelper.writeUserFile(path, fileName, content, app);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mapChangePopup.reloadStyles();
                })
                .setNegativeButton("No", null)
                .create();
    }

    public static AlertDialog askUserOverwriteFile(AppController app, String fileName, String path, String content, AlertDialog originDialog, FragmentMapChangePopup mapChangePopup) {
        return new AlertDialog.Builder(app.getActivity())
                .setMessage("Style of filename: " + fileName + " already exists, do you wish to overwrite it?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        FileHelper.writeUserFile(path, fileName, content, app);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (originDialog != null) {
                        originDialog.dismiss();
                    }
                    mapChangePopup.reloadStyles();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    if (originDialog != null) {
                        originDialog.dismiss();
                    }
                    dialog.dismiss();
                })
                .create();
    }

    public static AlertDialog pointSelector(AppController app, List<Feature> features, Consumer<Feature> callback) {
        LinearLayout layout = new LinearLayout(app.getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(app.getActivity());
        scrollView.addView(layout);

        AlertDialog dialog = new AlertDialog.Builder(app.getActivity())
                .setView(scrollView)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        for (Feature feature : features) {
            View itemView = PreviewItem.fromFeature(feature).makeView(app.getUi(), v -> {
                dialog.dismiss();
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.accept(feature));
            });
            layout.addView(itemView);
        }

        return dialog;
    }

    public static AlertDialog searchSettings(AppController app) {
        return new AlertDialog.Builder(app.getActivity())
                .setTitle("Configure Search")
                .setPositiveButton("OK", null)
                .create();
    }

    public static AlertDialog restartDialog(AppController app) {
        return new AlertDialog.Builder(app.getActivity())
                .setTitle("Restart required")
                .setMessage("Restart the app to apply changes.")
                .setCancelable(false)
                .setNegativeButton("Cancel", (d, w) -> {
                    Toast.makeText(app.getActivity(),
                            "Changes will be applied on next restart",
                            Toast.LENGTH_SHORT).show();
                    d.dismiss();
                })
                .setPositiveButton("Restart", (d, w) -> {
                    Intent i = app.getActivity().getPackageManager()
                            .getLaunchIntentForPackage(app.getActivity().getPackageName());
                    if (i != null) {
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        app.getActivity().startActivity(i);
                    }
                    Runtime.getRuntime().exit(0);
                })
                .create();
    }
}
