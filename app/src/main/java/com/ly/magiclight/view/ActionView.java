package com.ly.magiclight.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.ly.magiclight.R;
import com.ly.magiclight.utils.PixelUtil;

/**
 * @author ly
 * date 2021/3/16 13:37
 */
public class ActionView extends androidx.appcompat.widget.AppCompatImageView {

    private static final int DEFAULT_ALPHA = 50;
    private static final int STATE_SHOW = 1;
    private static final int STATE_HIDE = 2;
    private int state = STATE_HIDE;
    private AlertDialog colorDialog;
    private ColorfulLayout colorfulLayout;
    private ActionPopupWindow actionPopupWindow;

    public ActionView(Context context) {
        super(context);
        init();
    }

    public ActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setImageResource(R.mipmap.ic_action);
        setImageAlpha(DEFAULT_ALPHA);

        setOnClickListener(v -> {
            if (state == STATE_HIDE) {
                showActionPopup();
            }
            toggle();
        });

        actionPopupWindow = new ActionPopupWindow(getContext());
        actionPopupWindow.setOnDismissListener(() -> hide(500));
    }

    public void show() {

    }

    public void hide(int delay) {
        if (state != STATE_HIDE) {
            postDelayed(this::toggle, delay);
        }
    }

    private void toggle() {
        int start, end;
        float[] scales = new float[2];
        float[] rotations;
        if (state == STATE_HIDE) {
            start = DEFAULT_ALPHA;
            end = 255;
            scales[0] = 0.8f;
            scales[1] = 1.2f;

            rotations = new float[]{0f, 360f, 0f};
            state = STATE_SHOW;
        } else {
            start = 255;
            end = DEFAULT_ALPHA;

            scales[0] = 1.2f;
            scales[1] = 0.8f;

            rotations = new float[]{360f, 0f};
            state = STATE_HIDE;
        }

        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", start, end);
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("rotation", rotations);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", scales);
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", scales);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, alphaHolder, rotation, scaleYHolder, scaleXHolder);
        if (state == STATE_SHOW) {
            animator.setInterpolator(new BounceInterpolator());
        } else {
            animator.setInterpolator(new DecelerateInterpolator());
        }
        animator.setDuration(2000).start();
    }


    private void showActionPopup() {
        int[] point = new int[2];
        getLocationInWindow(point);
        actionPopupWindow.showAtLocation(this, Gravity.CENTER_HORIZONTAL, 0, point[1] / 3);
    }

    public void setColorfulLayout(ColorfulLayout colorfulLayout) {
        this.colorfulLayout = colorfulLayout;
        actionPopupWindow.setColorfulLayout(colorfulLayout);
    }
}
