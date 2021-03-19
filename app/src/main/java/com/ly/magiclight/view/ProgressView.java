package com.ly.magiclight.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.ly.magiclight.R;

/**
 * @author ly
 * date 2021/3/17 15:04
 */
public class ProgressView extends View {

    private float progress;
    private int progressColor;
    private Paint paint;
    private RectF rectF;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (progressColor == 0) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, defStyleAttr, 0);
            progressColor = typedArray.getColor(R.styleable.ProgressView_progressColor, Color.GREEN);
            typedArray.recycle();
        }

        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(progressColor);

        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        rectF.left = getLeft();
        rectF.top = getHeight();
        rectF.right = getRight();
        rectF.bottom = getBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rectF, paint);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;

        rectF.top = getHeight() - progress * getHeight();
        invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        if (paint != null)
            paint.setColor(progressColor);
    }
}
