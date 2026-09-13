package eu.konggdev.strikemaps.ui.factory;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.ComponentHolderActivity;
import eu.konggdev.strikemaps.storage.RegistryStorageComponent;
import eu.konggdev.strikemaps.util.json.JsonPatcher;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.map.style.management.StyleManagementMetadata;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.element.item.PreviewItem;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;
import org.maplibre.geojson.Feature;

import java.util.List;
import java.util.function.Consumer;

//FIXME: Cleaner architecture would be having a class for each AlertDialog type
public final class AlertDialogFactory {
    public static AlertDialog styleManagementOptions(AppCompatActivity activity, StyleManagementMetadata metadata) {
        String[] options = {
                "Update style",
                "Update automatically"
        };

        boolean[] checked = {
                metadata.doUpdates,
                metadata.autoUpdate
        };

        return new AlertDialog.Builder(activity)
                .setTitle("Built-in Style")
                .setMultiChoiceItems(options, checked, (dialog, which, isChecked) -> {
                    if (which == 0) {
                        metadata.doUpdates = isChecked;
                    } else if (which == 1) {
                        metadata.autoUpdate = isChecked;
                    }
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
                .create();
    }

    public static AlertDialog copyBuiltInStyle(AppCompatActivity activity, MapComponent map, UIComponent ui, FragmentMapChangePopup mapChangePopup) {
        //TODO: Use an UI element that's supposed to be vertical, instead of GenericItem

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(activity);
        scrollView.addView(container);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Copy from")
                .setView(scrollView)
                .setNegativeButton("Cancel", null)
                .create();

        return dialog;
    }

    public static AlertDialog createStyle(AppCompatActivity activity, RegistryStorageComponent registry, String baseStyleContents, FragmentMapChangePopup mapChangePopup) {
        final EditText nameInput = new EditText(activity);
        nameInput.setHint("Name");

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * activity.getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        container.addView(nameInput);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Create")
                .setView(container)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button createButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            createButton.setOnClickListener(v -> {
                ObjectMapper mactivityer = new ObjectMapper();
                boolean nameEntryRequired = true;
                String styleContentName = "";
                if (baseStyleContents != null) {
                    try {
                        JsonNode root = mactivityer.readTree(baseStyleContents);
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


                try {
                    JsonNode root;
                    if(baseStyleContents != null) {
                        if(!baseStyleContents.isEmpty()) {
                            root = mactivityer.readTree(baseStyleContents);
                        } else {
                            root = mactivityer.createObjectNode();
                        }
                    } else {
                        root = mactivityer.createObjectNode();
                    }

                    if (!root.path("name").asText().isEmpty()) {
                        ObjectNode node = mactivityer.createObjectNode();
                        node.put("name", name);
                        root = JsonPatcher.patch(root, node);
                    }

                    registry.addStyle(new MapStyle(
                        mactivityer.writeValueAsString(root),
                        new StyleOptions(),
                        null
                    ));
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(activity, "Failed to create", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                mapChangePopup.reloadStyles();
            });
        });

        return dialog;
    }

    public static AlertDialog pointSelector(ComponentHolderActivity activity, UIComponent ui, List<Feature> features, Consumer<Feature> callback) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(activity);
        scrollView.addView(layout);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(scrollView)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        for (Feature feature : features) {
            View itemView = PreviewItem.fromFeature(feature).makeView(ui, v -> {
                dialog.dismiss();
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.accept(feature));
            });
            layout.addView(itemView);
        }

        return dialog;
    }

    public static AlertDialog searchSettings(AppCompatActivity activity) {
        return new AlertDialog.Builder(activity)
                .setTitle("Configure Search")
                .setPositiveButton("OK", null)
                .create();
    }

    public static AlertDialog restartDialog(AppCompatActivity activity) {
        return new AlertDialog.Builder(activity)
                .setTitle("Restart required")
                .setMessage("Restart the activity to activityly changes.")
                .setCancelable(false)
                .setNegativeButton("Cancel", (d, w) -> {
                    Toast.makeText(activity,
                            "Changes will be activitylied on next restart",
                            Toast.LENGTH_SHORT).show();
                    d.dismiss();
                })
                .setPositiveButton("Restart", (d, w) -> {
                    Intent i = activity.getPackageManager()
                            .getLaunchIntentForPackage(activity.getPackageName());
                    if (i != null) {
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(i);
                    }
                    Runtime.getRuntime().exit(0);
                })
                .create();
    }
}
