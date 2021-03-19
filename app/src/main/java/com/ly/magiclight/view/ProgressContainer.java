package com.ly.magiclight.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ly.magiclight.R;
import com.ly.magiclight.utils.PixelUtil;

/**
 * @author ly
 * date 2021/3/17 16:00
 */
public class ProgressContainer extends LinearLayout implements ValueAnimator.AnimatorUpdateListener {
    private final int textColor;
    private final float textSize;
    private int progressColor;
    private ProgressView progressView;
    private float progress;
    private int adjustType;
    private Context mContext;
    private TextView tv_top;
    private TextView tv_bottom;
    private boolean isHide = true;
    private ObjectAnimator hideAnimator;
    private float curAlpha;

    public ProgressContainer(Context context) {
        this(context, null);
    }

    public ProgressContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressContainer, defStyleAttr, 0);
        progressColor = typedArray.getColor(R.styleable.ProgressContainer_progressColor1, Color.GREEN);
        textColor = typedArray.getColor(R.styleable.ProgressContainer_textColor, Color.BLACK);
        textSize = typedArray.getDimension(R.styleable.ProgressContainer_textSize, 20);
        typedArray.recycle();

        mContext = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setAlpha(0f);

        hideAnimator = ObjectAnimator.ofFloat(this, "alpha", curAlpha, 0f);
        hideAnimator.addUpdateListener(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() == 0) {
            progressView = new ProgressView(mContext);
            progressView.setProgressColor(progressColor);
            progressView.setBackgroundColor(Color.parseColor("#66000000"));

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            tv_top = new TextView(mContext);
            tv_top.setTextSize(textSize);
            tv_top.setTextColor(textColor);
            tv_top.setPadding(0, 0, 0, PixelUtil.dp2px(mContext, 15));

            tv_bottom = new TextView(mContext);
            tv_bottom.setTextSize(textSize);
            tv_bottom.setTextColor(textColor);
            tv_bottom.setPadding(0, PixelUtil.dp2px(mContext, 15), 0, 0);

            addView(tv_top, lp);
            addView(progressView, new LayoutParams(getMeasuredWidth(), PixelUtil.dp2px(getContext(), 200)));
            addView(tv_bottom, lp);
        }
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        show();
        this.progress = progress;
        progressView.setProgress(progress);
    }

    public void setAdjustType(int adjustType) {
        this.adjustType = adjustType;
        if (adjustType == ColorfulLayout.TYPE_BRIGHTNESS) {
            tv_top.setText("亮");
            tv_bottom.setText("暗");
        } else if (adjustType == ColorfulLayout.TYPE_VELOCITY) {
            tv_top.setText("快");
            tv_bottom.setText("慢");
        }
    }

    public void show() {
        if (isHide) {
            hideAnimator.cancel();
            hideAnimator.setFloatValues(curAlpha, 1.0f);
            hideAnimator.setDuration(600);
            hideAnimator.start();
            isHide = false;
        }
    }

    public void hide() {
        if (!isHide) {
            //此处为了避免显示动画还未执行完就执行隐藏动画出现的bug使用延时而不是setStartDelay
            postDelayed(() -> {
                hideAnimator.cancel();
                hideAnimator.setFloatValues(curAlpha, 0f);
                hideAnimator.setDuration(2000);
                hideAnimator.start();
                isHide = true;
            },800);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        curAlpha = (float) animation.getAnimatedValue();
    }

}
