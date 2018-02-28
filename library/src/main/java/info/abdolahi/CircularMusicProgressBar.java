package info.abdolahi;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import info.abdolahi.circularmusicbar.R;


public class CircularMusicProgressBar extends AppCompatImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final int DEFAULT_ANIMATION_TIME = 800;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_PROGRESS_COLOR = Color.BLUE;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private static final boolean DEFAULT_DRAW_ANTI_CLOCKWISE = false;
    private static float DEFAULT_INNTER_DAIMMETER_FRACTION = 0.805f;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float mBaseStartAngle = 0f;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mFillColor = DEFAULT_FILL_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mInnrCircleDiammeter;
    private float mDrawableRadius;
    private float mBorderRadius;
    private float mProgressValue = 0;
    private ValueAnimator mValueAnimator;
    private ColorFilter mColorFilter;
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;
    private boolean mDrawAntiClockwise;
    private boolean mDisableCircularTransformation;
    private boolean animationState = true;
    private OnCircularSeekBarChangeListener onChangeListener;


    public CircularMusicProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularMusicProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularMusicProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularMusicProgressBar, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircularMusicProgressBar_cmp_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircularMusicProgressBar_cmp_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircularMusicProgressBar_cmp_border_overlay, DEFAULT_BORDER_OVERLAY);
        mDrawAntiClockwise = a.getBoolean(R.styleable.CircularMusicProgressBar_cmp_draw_anticlockwise, DEFAULT_DRAW_ANTI_CLOCKWISE);
        mFillColor = a.getColor(R.styleable.CircularMusicProgressBar_cmp_fill_color, DEFAULT_FILL_COLOR);
        mInnrCircleDiammeter = a.getFloat(R.styleable.CircularMusicProgressBar_cmp_centercircle_diammterer, DEFAULT_INNTER_DAIMMETER_FRACTION);
        mProgressColor = a.getColor(R.styleable.CircularMusicProgressBar_cmp_progress_color, DEFAULT_PROGRESS_COLOR);
        mBaseStartAngle = a.getFloat(R.styleable.CircularMusicProgressBar_cmp_progress_startAngle, 0);

        a.recycle();
        init();
    }

    private void init() {

        setupGestureLitener(getContext());

        // init animator
        mValueAnimator = ValueAnimator.ofFloat(0, mProgressValue);
        mValueAnimator.setDuration(DEFAULT_ANIMATION_TIME);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setValueWithNoAnimation((float) valueAnimator.getAnimatedValue());
            }
        });

        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    public void setOnCircularBarChangeListener(OnCircularSeekBarChangeListener listener) {
        this.onChangeListener = listener;
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDisableCircularTransformation) {
            super.onDraw(canvas);
            return;
        }

        if (mBitmap == null) {
            return;
        }


        canvas.save();

        canvas.rotate(mBaseStartAngle, mDrawableRect.centerX(), mDrawableRect.centerY());

        if (mBorderWidth > 0) {
            mBorderPaint.setColor(mBorderColor);
            canvas.drawArc(mBorderRect, 0, 360, false, mBorderPaint);
        }

        mBorderPaint.setColor(mProgressColor);

        float sweetAngle = mProgressValue / 100 * 360;
        canvas.drawArc(mBorderRect, 0, mDrawAntiClockwise ? -sweetAngle : sweetAngle, false, mBorderPaint);

        canvas.restore();

        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        if (mFillColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mFillPaint);
        }

    }

    public void setValue(float newValue) {
        if (animationState) {

            if (mValueAnimator.isRunning()) {
                mValueAnimator.cancel();
            }

            mValueAnimator.setFloatValues(mProgressValue, newValue);
            mValueAnimator.start();
        } else {
            setValueWithNoAnimation(newValue, false);
        }

    }

    public void setValueWithNoAnimation(float newValue) {
        setValueWithNoAnimation(newValue, false);
    }

    public void setValueWithNoAnimation(float newValue, boolean fromUser) {
        if (onChangeListener != null) {
            onChangeListener.onProgressChanged(this, (int) newValue, fromUser);
        }
        mProgressValue = newValue;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    /**
     * Change state of progress value animation. set it to 'false' if you don't want any animation
     *
     * @param state boolean state of progress animation. if set to false, no animation happen whenever value is changed
     */
    public void setProgressAnimationState(boolean state) {
        animationState = state;
    }

    /**
     * change interpolator of animation to get more effect on animation
     *
     * @param interpolator animation interpolator
     */
    public void setProgressAnimatorInterpolator(TimeInterpolator interpolator) {
        mValueAnimator.setInterpolator(interpolator);
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setBorderProgressColor(@ColorInt int borderColor) {
        if (borderColor == mProgressColor) {
            return;
        }

        mProgressColor = borderColor;
        invalidate();
    }

    /**
     * @deprecated Use {@link #setBorderColor(int)} instead
     */
    @Deprecated
    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }

    /**
     * Return the color drawn behind the circle-shaped drawable.
     *
     * @return The color drawn behind the drawable
     * @deprecated Fill color support is going to be removed in the future
     */
    @Deprecated
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColor The color to be drawn behind the drawable
     * @deprecated Fill color support is going to be removed in the future
     */
    @Deprecated
    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }

        mFillColor = fillColor;
        mFillPaint.setColor(fillColor);
        invalidate();
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColorRes The color resource to be resolved to a color and
     *                     drawn behind the drawable
     * @deprecated Fill color support is going to be removed in the future
     */
    @Deprecated
    public void setFillColorResource(@ColorRes int fillColorRes) {
        setFillColor(getContext().getResources().getColor(fillColorRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    public boolean isDisableCircularTransformation() {
        return mDisableCircularTransformation;
    }

    public void setDisableCircularTransformation(boolean disableCircularTransformation) {
        if (mDisableCircularTransformation == disableCircularTransformation) {
            return;
        }

        mDisableCircularTransformation = disableCircularTransformation;
        initializeBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    private void applyColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.setColorFilter(mColorFilter);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        if (mDisableCircularTransformation) {
            mBitmap = null;
        } else {
            mBitmap = getBitmapFromDrawable(getDrawable());
        }
        setup();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(calculateBounds());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }

        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        if (mInnrCircleDiammeter > 1) mInnrCircleDiammeter = 1;

        mDrawableRadius = mDrawableRadius * mInnrCircleDiammeter;

        applyColorFilter();
        updateShaderMatrix();
        invalidate();
    }


    private static int getMeasurementSize(int measureSpec, int defaultSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;

            case MeasureSpec.AT_MOST:
                return Math.min(defaultSize, size);

            case MeasureSpec.UNSPECIFIED:
            default:
                return defaultSize;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasurementSize(widthMeasureSpec, 600);
        int height = getMeasurementSize(heightMeasureSpec, 600);
        setMeasuredDimension(width, height);
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left + getBorderWidth(), top + getBorderWidth(), left + sideLength - getBorderWidth(), top + sideLength - getBorderWidth());
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }


    /**
     * ------------------------------------------
     * ------------------------------------------
     * ------------ All Things about touch ------
     * ------------------------------------------
     * ------------------------------------------
     */

    private GestureDetector gestureListener;

    public void setupGestureLitener(Context context) {
        gestureListener = new GestureDetector(context, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                if (computeInArea(motionEvent.getX(), motionEvent.getY())) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (onChangeListener != null) {
                        onChangeListener.onClick(CircularMusicProgressBar.this);
                    }
                    postInvalidate();
                    return true;
                } else if (computeAndSetAngle(motionEvent.getX(), motionEvent.getY())) {
                    return true;
                }
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                endGesture();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                computeAndSetAngle(motionEvent1.getX(), motionEvent1.getY());
                postInvalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if (onChangeListener != null) {
                    onChangeListener.onLongPress(CircularMusicProgressBar.this);
                }
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
    }

    private boolean computeInArea(float x, float y) {
        x -= getWidth() / 2;
        y -= getHeight() / 2;
        return (Math.sqrt(x * x + y * y) <= ((mDrawableRadius / 3) * 2));
    }

    private boolean computeAndSetAngle(float x, float y) {

        float circleDiameter = mDrawableRadius + mBorderWidth;

        x -= getWidth() / 2;
        y -= getHeight() / 2;

        double radius = Math.sqrt(x * x + y * y);
        if (radius > circleDiameter || radius < ((mDrawableRadius / 3) * 2)) {
            return false;
        }

        int angle;
        if (mDrawAntiClockwise) {
            angle = (int) ((180.0 * Math.atan2(x, y) / Math.PI) - mBaseStartAngle);
        } else {
            angle = (int) ((180.0 * Math.atan2(y, x) / Math.PI) - mBaseStartAngle);
        }
        angle = ((angle > 0) ? angle : 360 + angle);
        float intoPercent = angle * 100 / 360;
        setValueWithNoAnimation(intoPercent, true);
        return true;
    }

    private void endGesture() {
        getParent().requestDisallowInterceptTouchEvent(false);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureListener.onTouchEvent(event);
    }
}
