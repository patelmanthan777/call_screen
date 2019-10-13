package com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.controller;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.Value;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.ColorAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.DropAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.FillAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.ScaleAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.ScaleDownAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.SlideAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.SwapAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.ThinWormAnimation;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.type.WormAnimation;

public class ValueController {

    private ColorAnimation colorAnimation;
    private ScaleAnimation scaleAnimation;
    private WormAnimation wormAnimation;
    private SlideAnimation slideAnimation;
    private FillAnimation fillAnimation;
    private ThinWormAnimation thinWormAnimation;
    private DropAnimation dropAnimation;
    private SwapAnimation swapAnimation;
    private ScaleDownAnimation scaleDownAnimation;

    private UpdateListener updateListener;

    public interface UpdateListener {
        void onValueUpdated(Value value);
    }

    public ValueController( UpdateListener listener) {
        updateListener = listener;
    }

    public ColorAnimation color() {
        if (colorAnimation == null) {
            colorAnimation = new ColorAnimation(updateListener);
        }

        return colorAnimation;
    }

    public ScaleAnimation scale() {
        if (scaleAnimation == null) {
            scaleAnimation = new ScaleAnimation(updateListener);
        }

        return scaleAnimation;
    }
    public WormAnimation worm() {
        if (wormAnimation == null) {
            wormAnimation = new WormAnimation(updateListener);
        }

        return wormAnimation;
    }

    public SlideAnimation slide() {
        if (slideAnimation == null) {
            slideAnimation = new SlideAnimation(updateListener);
        }

        return slideAnimation;
    }

    public FillAnimation fill() {
        if (fillAnimation == null) {
            fillAnimation = new FillAnimation(updateListener);
        }

        return fillAnimation;
    }

    public ThinWormAnimation thinWorm() {
        if (thinWormAnimation == null) {
            thinWormAnimation = new ThinWormAnimation(updateListener);
        }

        return thinWormAnimation;
    }

    public DropAnimation drop() {
        if (dropAnimation == null) {
            dropAnimation = new DropAnimation(updateListener);
        }

        return dropAnimation;
    }


    public SwapAnimation swap() {
        if (swapAnimation == null) {
            swapAnimation = new SwapAnimation(updateListener);
        }

        return swapAnimation;
    }

    public ScaleDownAnimation scaleDown() {
        if (scaleDownAnimation == null) {
            scaleDownAnimation = new ScaleDownAnimation(updateListener);
        }

        return scaleDownAnimation;
    }
}
