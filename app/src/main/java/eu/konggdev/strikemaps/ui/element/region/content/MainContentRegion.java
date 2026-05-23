package eu.konggdev.strikemaps.ui.element.region.content;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import eu.konggdev.strikemaps.ui.element.region.UIRegion;

public class MainContentRegion extends UIRegion {
    public Fragment fragment;
    public Integer layoutId;

    public MainContentRegion(@NonNull Fragment initFragment, Integer refLayoutId) {
        super(initFragment, refLayoutId);
        this.fragment = initFragment;
        this.layoutId = refLayoutId;
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
