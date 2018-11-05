package cn.acewill.pos.next.utils;

/**
 * Created by DHH on 2017/5/17.
 */

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class ScaleAnimEffect {
    private long duration;
    private float fromXScale;
    private float fromYScale;
    private float toXScale;
    private float toYScale;

    public void setAttributs(float paramFloat1, float paramFloat2,
                             float paramFloat3, float paramFloat4, long paramLong) {
        this.fromXScale = paramFloat1;
        this.fromYScale = paramFloat3;
        this.toXScale = paramFloat2;
        this.toYScale = paramFloat4;
        this.duration = paramLong;
    }

    public Animation createAnimation() {
        /**
         * @param fromX
         *            动画开始前水平方向的伸缩比例大小
         * @param toX
         *            动画结束后，水平方向的伸缩比例大小
         * @param fromY
         *            动画开始前，竖直方向的比例大小
         * @param toY
         *            动画结束结束后，竖直方向的比例大小
         * @param pivotXType
         *            指定pivotXValue以哪个为坐标点为中心来旋转。 Animation.ABSOLUTE,
         *            Animation.RELATIVE_TO_SELF, 或者
         *            Animation.RELATIVE_TO_PARENT这三个其中之一。
         * @param pivotXValue
         *            正在伸缩的对象的点的x坐标，指定为绝对数，并且0是左边缘（当对象改变尺寸的时候，点保持不变。）
         *            如果pivotXType是
         *            Animation.ABSOLUTE，这个值既可以是绝对数，也可以为百分数（1.0位100%）
         *
         * @param pivotYType
         *            指定pivotYValue以哪个为坐标点为中心来旋转。 Animation.ABSOLUTE,
         *            Animation.RELATIVE_TO_SELF, 或者
         *            Animation.RELATIVE_TO_PARENT这三个其中之一。
         * @param pivotYValue
         *            正在伸缩的对象的点的y坐标，指定为绝对数，并且0是左边缘（当对象改变尺寸的时候，点保持不变。）
         *            如果pivotYType是
         *            Animation.ABSOLUTE，这个值既可以是绝对数，也可以为百分数（1.0位100%）
         */
        ScaleAnimation localScaleAnimation = new ScaleAnimation(
                this.fromXScale, this.toXScale, this.fromYScale, this.toYScale,
                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF,
                0.5F);
        // 动画执行完成后，是否停留在执行完的状态
        localScaleAnimation.setFillAfter(true);
        // 在动画开始的地方速率比较慢，然后开始加速
        localScaleAnimation.setInterpolator(new AccelerateInterpolator());
        // 设置动画持续时间
        localScaleAnimation.setDuration(this.duration);
        return localScaleAnimation;
    }

}