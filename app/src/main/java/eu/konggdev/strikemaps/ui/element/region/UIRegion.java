package eu.konggdev.strikemaps.ui.element.region;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.ui.fragment.layout.Layout;

import java.util.ArrayDeque;

public class UIRegion {
    private final ArrayDeque<Fragment> previousFragments = new ArrayDeque<>();

    private Fragment stockFragment;
    private Fragment currentFragment;
    public Integer layoutId;

    public UIRegion(@NonNull Fragment initFragment, Integer refLayoutId) {
        this.currentFragment = initFragment;
        this.stockFragment = initFragment;

        this.layoutId = refLayoutId;
    }

    public Fragment getFragment() {
        return currentFragment;
    }

    public void setFragment(Fragment fragment) {
        previousFragments.add(currentFragment);
        currentFragment = fragment;
    }

    public void overwriteStockFragment(Fragment fragment) {
        stockFragment = fragment;
    }

    public void back() {
        if (!previousFragments.isEmpty()) {
            currentFragment = previousFragments.pop();
        } else {
            currentFragment = stockFragment;
        }
    }
}
