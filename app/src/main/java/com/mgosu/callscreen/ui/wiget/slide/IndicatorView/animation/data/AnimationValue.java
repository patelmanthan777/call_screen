package com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data;

import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.ColorAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.DropAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.FillAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.ScaleAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.SwapAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.ThinWormAnimationValue;
import com.mgosu.callscreen.ui.wiget.slide.IndicatorView.animation.data.type.WormAnimationValue;

public class AnimationValue {

    private ColorAnimationValue colorAnimationValue;
    private ScaleAnimationValue scaleAnimationValue;
    private WormAnimationValue wormAnimationValue;
    private FillAnimationValue fillAnimationValue;
    private ThinWormAnimationValue thinWormAnimationValue;
    private DropAnimationValue dropAnimationValue;
    private SwapAnimationValue swapAnimationValue;

    public ColorAnimationValue getColorAnimationValue() {
        if (colorAnimationValue == null) {
            colorAnimationValue = new ColorAnimationValue();
        }
        return colorAnimationValue;
    }

    public ScaleAnimationValue getScaleAnimationValue() {
        if (scaleAnimationValue == null) {
            scaleAnimationValue = new ScaleAnimationValue();
        }
        return scaleAnimationValue;
    }

    public WormAnimationValue getWormAnimationValue() {
        if (wormAnimationValue == null) {
            wormAnimationValue = new WormAnimationValue();
        }
        return wormAnimationValue;
    }

    public FillAnimationValue getFillAnimationValue() {
        if (fillAnimationValue == null) {
            fillAnimationValue = new FillAnimationValue();
        }
        return fillAnimationValue;
    }
    public ThinWormAnimationValue getThinWormAnimationValue() {
        if (thinWormAnimationValue == null) {
            thinWormAnimationValue = new ThinWormAnimationValue();
        }
        return thinWormAnimationValue;
    }

    public DropAnimationValue getDropAnimationValue() {
        if (dropAnimationValue == null) {
            dropAnimationValue = new DropAnimationValue();
        }
        return dropAnimationValue;
    }

    public SwapAnimationValue getSwapAnimationValue() {
        if (swapAnimationValue == null) {
            swapAnimationValue = new SwapAnimationValue();
        }
        return swapAnimationValue;
    }
}
