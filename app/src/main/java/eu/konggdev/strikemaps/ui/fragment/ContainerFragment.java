package eu.konggdev.strikemaps.ui.fragment;

import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

public interface ContainerFragment {
    abstract public Integer getRegion();

    abstract public Fragment toFragment();


    //Helper methods (ugly)
    //FIXME
    default void setupButton(View view, int button, View.OnClickListener onClick) {
        view.findViewById(button)
                .setOnClickListener(onClick);
    }

    default void setupButton(View view, int button, View.OnClickListener onClick, View.OnLongClickListener onLongClick) {
        View buttonView = view.findViewById(button);
        buttonView.setOnClickListener(onClick);
        buttonView.setOnLongClickListener(onLongClick);
    }

    default void setupButton(View view, int button, View.OnLongClickListener onLongClick) {
        view.findViewById(button)
                .setOnLongClickListener(onLongClick);
    }

    default View.OnClickListener click(Runnable action) {
        return v -> action.run();
    }

    default View.OnLongClickListener longClick(Runnable action) { return v -> { action.run(); return true; };}

    //TODO: Make animation less wonky
    default void setupDragHandle(View dragHandle, View layout, Runnable closeAction) {
        final float[] dY = new float[1];
        dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dY[0] = event.getRawY() - layout.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newY = event.getRawY() - dY[0];
                        if (newY >= 0) {
                            layout.setY(newY);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (layout.getY() > layout.getHeight() / 4) {
                            layout.animate()
                                    .scaleX(0f)
                                    .scaleY(0f)
                                    .alpha(0f)
                                    .setDuration(300)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            layout.setVisibility(View.GONE);
                                            closeAction.run();
                                            layout.setScaleX(1f);
                                            layout.setScaleY(1f);
                                            layout.setAlpha(1f);
                                            layout.setY(0f);
                                        }
                                    })
                                    .start();
                        } else {
                            layout.animate()
                                    .translationY(0f)
                                    .setDuration(200)
                                    .start();
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
    }
}
