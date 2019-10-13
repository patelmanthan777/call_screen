package com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation;


import android.support.annotation.NonNull;

import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.controller.AnimationController;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.controller.ValueController;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.draw.data.Indicator;

public class AnimationManager {

    private AnimationController animationController;

    public AnimationManager(@NonNull Indicator indicator, @NonNull ValueController.UpdateListener listener) {
        this.animationController = new AnimationController(indicator, listener);
    }

    public void basic() {
        if (animationController != null) {
            animationController.end();
            animationController.basic();
        }
    }

    public void interactive(float progress) {
        if (animationController != null) {
            animationController.interactive(progress);
        }
    }

    public void end() {
        if (animationController != null) {
            animationController.end();
        }
    }
}
