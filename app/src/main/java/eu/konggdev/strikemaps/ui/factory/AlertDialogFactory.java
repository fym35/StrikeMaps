package eu.konggdev.strikemaps.ui.factory;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.ui.element.item.PreviewItem;
import org.maplibre.geojson.Feature;

import java.util.List;
import java.util.function.Consumer;

//FIXME: Move Item functions into specific classes for specific types - e.g. StyleItem
public final class AlertDialogFactory {
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
                new android.os.Handler(android.os.Looper.getMainLooper())
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
}
