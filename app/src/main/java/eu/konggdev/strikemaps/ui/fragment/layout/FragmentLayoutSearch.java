package eu.konggdev.strikemaps.ui.fragment.layout;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import eu.konggdev.strikemaps.R;
import eu.konggdev.strikemaps.app.AppController;
import eu.konggdev.strikemaps.ui.factory.AlertDialogFactory;
import eu.konggdev.strikemaps.ui.screen.definition.DefinedScreen;

public class FragmentLayoutSearch extends Fragment implements Layout {
    AppController app;
    private final Integer region;

    public FragmentLayoutSearch(AppController app, Integer region) {
        super(R.layout.fragment_search);
        this.app = app;
        this.region = region;
    }

    @Override
    public Integer getRegion() {
        return region;
    }

    @Override
    public Fragment toFragment() {
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        setupButton(view, R.id.hamburgerButton, click(() -> {

            View menuView = getLayoutInflater().inflate(R.layout.dropdown_main, null);

            PopupWindow popupWindow = new PopupWindow(
                    menuView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            setupButton(menuView, R.id.menuSearchSettings, click(() -> {
                app.getUi().alert(AlertDialogFactory.searchSettings(app));
            }));

            setupButton(menuView, R.id.menuSettings, click(() -> {
                popupWindow.dismiss();
                app.getUi().swapScreen(DefinedScreen.SETTINGS);
            }));

            menuView.findViewById(R.id.menuSearchSettings).setOnClickListener(v -> popupWindow.dismiss());
            menuView.findViewById(R.id.menuAbout).setOnClickListener(v -> popupWindow.dismiss());

            View anchor = view.findViewById(R.id.searchContainer);

            anchor.post(() -> {

                menuView.measure(
                        View.MeasureSpec.UNSPECIFIED,
                        View.MeasureSpec.UNSPECIFIED
                );

                int popupWidth = menuView.getMeasuredWidth();
                int containerWidth = anchor.getWidth();

                int xOffset = containerWidth - popupWidth;
                popupWindow.showAsDropDown(anchor, xOffset, 1);
            });

        }));

        setupButton(view, R.id.offlineMapsButton, click(() -> {
            View offlineMapsPopupView = getLayoutInflater().inflate(R.layout.dropdown_offline, null);

            PopupWindow popupWindow = new PopupWindow(
                    offlineMapsPopupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            View anchor = view.findViewById(R.id.searchContainer);

            setupButton(offlineMapsPopupView, R.id.offlineMaps, click(() -> {
                popupWindow.dismiss();
                app.getUi().swapScreen(DefinedScreen.OFFLINE);
            }));

            anchor.post(() -> {

                offlineMapsPopupView.measure(
                        View.MeasureSpec.UNSPECIFIED,
                        View.MeasureSpec.UNSPECIFIED
                );

                int popupWidth = offlineMapsPopupView.getMeasuredWidth();
                int containerWidth = anchor.getWidth();

                int xOffset = containerWidth - popupWidth;
                popupWindow.showAsDropDown(anchor, xOffset, 1);
            });
        }));
    }
}
