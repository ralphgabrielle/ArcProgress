package project.forward.com.approundedarc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by forward on 6/3/2016.
 */
public class ArcCircle extends View {

    Paint arcPaint, trackPaint, percentPaint, percentContainerPaint, textPaint;

    private int color;
    private int stroke, thumbStroke;
    private float progress;
    private boolean startAnimation;
    private long duration;
    private int textColor;
    private float textSize;

    public ArcCircle(Context context) {
        super(context);
        init(context, null);
        startAnimation();
    }

    public ArcCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        startAnimation();
    }

    public ArcCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        startAnimation();
    }

    private void startAnimation() {
        if (startAnimation) {
            ArcAnimation arcAnimation = new ArcAnimation(this, progress);
            arcAnimation.setDuration(duration * 1000);
            startAnimation(arcAnimation);
            requestLayout();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcCircle);

        color = typedArray.getColor(R.styleable.ArcCircle_progressColor, Color.BLUE);
        stroke = (int) typedArray.getDimension(R.styleable.ArcCircle_stroke, 50);
        progress = typedArray.getFloat(R.styleable.ArcCircle_progress, 50);
        startAnimation = typedArray.getBoolean(R.styleable.ArcCircle_startAnimation, true);
        duration = typedArray.getInteger(R.styleable.ArcCircle_duration, 2000);
        textColor = typedArray.getColor(R.styleable.ArcCircle_textColor, Color.WHITE);
        textSize = typedArray.getDimension(R.styleable.ArcCircle_textSize, 20);
        thumbStroke = (int) typedArray.getDimension(R.styleable.ArcCircle_stroke, 50);

        typedArray.recycle();
        arcPaint = createPaint(color, stroke);
        trackPaint = createPaint(Color.GRAY, stroke);

        percentPaint = createPaint(color, thumbStroke);
        percentPaint.setStyle(Paint.Style.FILL);

        percentContainerPaint = createPaint(Color.WHITE, thumbStroke);
        percentContainerPaint.setStyle(Paint.Style.FILL);

        setTextPaint();
    }

    private Paint createPaint(int color, int stroke) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.STROKE);//
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        return paint;
    }

    private void setTextPaint() {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }

    private void drawArc(Canvas canvas) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        float radius;

        if (width > height) {
            radius = height / 4;
        } else {
            radius = width / 4;
        }

        float center_x, center_y;
        final RectF oval = new RectF();

        center_x = width / 2;
        center_y = height / 2;

        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);

        float arcRadius = 360;
        float angle = arcRadius * (progress/100);

        float endX = (float) (Math.cos(Math.toRadians(270 + angle)) * radius + center_x);
        float endY = (float) (Math.sin(Math.toRadians(270 + angle)) * radius + center_y);

        canvas.drawArc(oval, 270, 360, false, trackPaint);
        canvas.drawArc(oval, 270, angle, false, arcPaint);
        canvas.drawCircle(endX, endY, 60, percentContainerPaint);
        canvas.drawCircle(endX, endY, 50, percentPaint);

        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();

        canvas.drawText((int)progress + "%", endX, endY + textOffset, textPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    public class ArcAnimation extends Animation {

        ArcCircle circle;
        float newProgress;
        float oldProgress;

        public ArcAnimation(ArcCircle circle, float newProgress) {
            this.circle = circle;
            this.newProgress = newProgress;
            this.oldProgress = 0;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float progress = oldProgress + ((newProgress - oldProgress) * interpolatedTime);

            circle.setProgress(progress);
            circle.requestLayout();
        }
    }
}