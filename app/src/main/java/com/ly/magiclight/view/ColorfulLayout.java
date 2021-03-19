package com.ly.magiclight.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ly.magiclight.MainActivity;
import com.ly.magiclight.utils.ScreenUtil;

import java.util.Random;

/**
 * 左边上下滑动调整动画快慢
 * 右边上下滑动调整画面明暗
 *
 * @author ly
 * date 2021/3/12 14:35
 */
public class ColorfulLayout extends RelativeLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "ColorfulLayout";
    private float lastY;
    private float brightnessFactor;
    private float velocityFactor;
    public static int TYPE_BRIGHTNESS = 1;
    public static int TYPE_VELOCITY = 2;
    private int adjustType;
    private int touchSlop;
    //滑动因子，越大滑动越灵敏
    private static final float TOUCH_FACTOR = 0.1f;
    private static final int START_LOOP = 1;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    private ArgbEvaluator argbEvaluator;
    public static final String[] MODE = {"温馨", "激情", "动感", "单色"};
    private String curMode = MODE[0];
    private ValueAnimator animator;
    private ProgressContainer progressContainer;
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ColorDrawable colordDrawable = (ColorDrawable) getBackground();
            int oldColor = colordDrawable.getColor();
            int newColor = getRandomColor();
            Log.e(TAG, "oldColor:" + oldColor + " newColor:" + newColor);
            changeColor(oldColor, newColor);
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        progressContainer = (ProgressContainer) getChildAt(0);
    }

    public ColorfulLayout(@NonNull Context context) {
        this(context, null);
    }

    public ColorfulLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorfulLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.YELLOW);
        handler.sendEmptyMessage(START_LOOP);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        //最小滑动值
        touchSlop = viewConfiguration.getScaledTouchSlop();

        brightnessFactor = ScreenUtil.getCurrentScreenBrightness((MainActivity) getContext());
    }

    private void changeColor(int oldColor, int newColor) {
        animator = ValueAnimator.ofInt(oldColor, newColor);
        int duration, delay;
        if (curMode.equals(MODE[0])) {
            duration = 2000;
            delay = 1000;
        } else if (curMode.equals(MODE[1])) {
            duration = 200;

            delay = new Random().nextInt(200) + 100;
        } else if (curMode.equals(MODE[2])) {
            duration = 20;
            delay = new Random().nextInt(500);
        } else {
            return;
        }
        duration = (int) (duration - duration * velocityFactor * 0.1);
        delay = (int) (delay - delay * velocityFactor * 0.8);

        animator.setDuration(duration);
        if (accelerateDecelerateInterpolator == null)
            accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        animator.setInterpolator(accelerateDecelerateInterpolator);
        if (argbEvaluator == null)
            argbEvaluator = new ArgbEvaluator();
        animator.setEvaluator(argbEvaluator);
        animator.removeAllUpdateListeners();
        animator.addUpdateListener(this);
        animator.start();

        handler.sendEmptyMessageDelayed(START_LOOP, duration + delay);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                //状态栏区域不响应
                if (lastY < ScreenUtil.getStatusBarHeight(getContext()))
                    return false;

                adjustType = event.getX() < getWidth() / 2f ? TYPE_VELOCITY : TYPE_BRIGHTNESS;
                progressContainer.setAdjustType(adjustType);
                break;
            case MotionEvent.ACTION_UP:
                progressContainer.hide();
                break;
            case MotionEvent.ACTION_MOVE:
                float moved = lastY - event.getY();
                if (Math.abs(moved) < touchSlop)
                    return true;
                float factor = moved * TOUCH_FACTOR / ScreenUtil.getScreenHeight(getContext());

                if (adjustType == TYPE_BRIGHTNESS) {
                    this.brightnessFactor += factor;
                    if (this.brightnessFactor > 1f) this.brightnessFactor = 1f;
                    if (this.brightnessFactor < 0f) this.brightnessFactor = 0f;
                    progressContainer.setProgress(brightnessFactor);
                    ScreenUtil.setCurrentScreenBrightness((MainActivity) getContext(), this.brightnessFactor);
                } else {
                    if (!curMode.equals(MODE[3])) {//单色模式不能调节快慢
                        velocityFactor += factor;
                        if (this.velocityFactor > 1f) this.velocityFactor = 1f;
                        if (this.velocityFactor < 0f) this.velocityFactor = 0f;
                        progressContainer.setProgress(velocityFactor);
                    } else {
                        Toast.makeText(getContext(), "当前模式不支持调节快慢", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:

                break;
        }
        Log.d(TAG, "adjustType:" + adjustType + " brightnessFactor:" + brightnessFactor + " velocityFactor:" + velocityFactor + " touchSlop:" + touchSlop);

        return true;
    }

    public void changeMode(String mode) {
        if (getIndexByMode(mode) < 0 || curMode.equals(mode))
            return;
        curMode = mode;
        stopLoop();
        handler.sendEmptyMessage(START_LOOP);
    }

    public void setSingleColor(int color) {
        curMode = MODE[3];
        stopLoop();
        setBackgroundColor(color);
    }

    private void stopLoop() {
        handler.removeCallbacksAndMessages(null);
        animator.cancel();
        animator.removeAllUpdateListeners();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (curMode.equals(MODE[3]))
            return;
        int curValue = (int) animation.getAnimatedValue();
        setBackgroundColor(curValue);
    }

    private int getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        int argb = Color.argb(255, r, g, b);
        return getBrighterColor(argb);
    }

    /**
     * HSV是一种颜色模型，
     * hsv[0] 是色调(Hue)，取值范围是0到360；
     * hsv[1] 是饱和度( Saturation)，取值范围是0到1，值越高，颜色越接近光谱色；
     * hsv[2] 是明度( Value )，取值范围是0到1。
     */
    public int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = 1;
        hsv[2] = 1;
        return Color.HSVToColor(hsv);
    }

    public String getCurMode() {
        return curMode;
    }

    public int getIndexByMode(String mode) {
        for (int i = 0; i < MODE.length; i++) {
            if (MODE[i].equals(mode))
                return i;
        }
        return -1;
    }
}
