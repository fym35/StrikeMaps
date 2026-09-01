package eu.konggdev.strikemaps.ui.factory;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.app.util.JsonPatcher;
import eu.konggdev.strikemaps.map.MapComponent;
import eu.konggdev.strikemaps.map.style.MapStyle;
import eu.konggdev.strikemaps.map.style.options.StyleOptions;
import eu.konggdev.strikemaps.ui.UIComponent;
import eu.konggdev.strikemaps.ui.element.item.PreviewItem;
import eu.konggdev.strikemaps.ui.fragment.popup.FragmentMapChangePopup;
import org.maplibre.geojson.Feature;

import java.util.List;
import java.util.function.Consumer;


//FIXME: Cleaner architecture would be having a class for each AlertDialog type
public final class AlertDialogFactory {
    public static AlertDialog copyBuiltInStyle(AppController app, MapComponent map, UIComponent ui, FragmentMapChangePopup mapChangePopup) {
        //TODO: Use an UI element that's supposed to be vertical, instead of GenericItem

        LinearLayout container = new LinearLayout(app.getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

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

        LinearLayout container = new LinearLayout(app.getActivity());
        container.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * app.getActivity().getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        container.addView(nameInput);

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

                    app.getRegistry().addStyle(new MapStyle(
                        mapper.writeValueAsString(root),
                        new StyleOptions(),
                        null
                    ));
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
