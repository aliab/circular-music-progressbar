@file:JvmName("CircularMusicProgressBar")
package info.abdolahi;


import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import info.abdolahi.circularmusicbar.R
import kotlin.math.atan2
import kotlin.math.sqrt

open class CircularMusicProgressBar(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
        private const val DEFAULT_ANIMATION_TIME = 800
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_FILL_COLOR = Color.TRANSPARENT
        private const val DEFAULT_PROGRESS_COLOR = Color.BLUE
        private const val DEFAULT_INDETERMINATE_ANGLE = 30
        private const val DEFAULT_BORDER_OVERLAY = false
        private const val DEFAULT_DRAW_ANTI_CLOCKWISE = false
        private const val DEFAULT_ENABLE_TOUCH = false
        private const val DEFAULT_INDETERMINATE = false
        private const val DEFAULT_INNTER_DAIMMETER_FRACTION = 0.805f
        private val mDrawableRect = RectF()
        private val mBorderRect = RectF()
        private val mShaderMatrix = Matrix()
        private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    var mBaseStartAngle = 0f
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mFillColor = DEFAULT_FILL_COLOR
    private var mProgressColor = DEFAULT_PROGRESS_COLOR
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mInnrCircleDiammeter = 0f
    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f
    private var mProgressValue = 0f
    private var mIndeterminateAngle = 0f
    private var mBaseAngle = 0f
    private var mValueAnimator: ValueAnimator? = null
    private var mAngleAnimator: ValueAnimator? = null
    private var mColorFilter: ColorFilter? = null
    private var mReady = false
    private var mSetupPending = false
    private var mBorderOverlay = false
    private var mDrawAntiClockwise = false
    private var mEnableTouch = false
    private var mDisableCircularTransformation = false
    private var animationState = true
    private var mIndeterminate = false
    private var onChangeListener: OnCircularSeekBarChangeListener? = null

    constructor(context: Context) : this(context, null, 0) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        if (attrs != null) {
            resolveAttributes(context, attrs, defStyle)
            init()
        }
    }

    private fun resolveAttributes(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.CircularMusicProgressBar,
            defStyle,
            0
        ).apply {
            mBorderWidth = getDimensionPixelSize(
                R.styleable.CircularMusicProgressBar_border_width,
                DEFAULT_BORDER_WIDTH
            )
            mBorderColor = getColor(
                R.styleable.CircularMusicProgressBar_border_color,
                DEFAULT_BORDER_COLOR
            )
            mBorderOverlay = getBoolean(
                R.styleable.CircularMusicProgressBar_border_overlay,
                DEFAULT_BORDER_OVERLAY
            )
            mDrawAntiClockwise = getBoolean(
                R.styleable.CircularMusicProgressBar_draw_anticlockwise,
                DEFAULT_DRAW_ANTI_CLOCKWISE
            )
            mEnableTouch = getBoolean(
                R.styleable.CircularMusicProgressBar_enable_touch,
                DEFAULT_ENABLE_TOUCH
            )
            mFillColor = getColor(
                R.styleable.CircularMusicProgressBar_fill_color,
                DEFAULT_FILL_COLOR
            )
            mInnrCircleDiammeter = getFloat(
                R.styleable.CircularMusicProgressBar_centercircle_diammterer,
                DEFAULT_INNTER_DAIMMETER_FRACTION
            )
            mProgressColor = getColor(
                R.styleable.CircularMusicProgressBar_progress_color,
                DEFAULT_PROGRESS_COLOR
            )
            mIndeterminate = getBoolean(
                R.styleable.CircularMusicProgressBar_indeterminate,
                DEFAULT_INDETERMINATE
            )
            mIndeterminateAngle = getFloat(
                R.styleable.CircularMusicProgressBar_indeterminate_angle,
                DEFAULT_INDETERMINATE_ANGLE.toFloat()
            )
            mBaseStartAngle =
                getFloat(R.styleable.CircularMusicProgressBar_progress_startAngle, 0f)
            mBaseAngle = mBaseStartAngle
        }.recycle()
    }

    private fun init() {
        if (mEnableTouch) {
            setupGestureLitener(context)
        }

        // init animator
        mValueAnimator = ValueAnimator.ofFloat(0f, mProgressValue)
        mValueAnimator?.setDuration(DEFAULT_ANIMATION_TIME.toLong())
        mValueAnimator?.addUpdateListener { valueAnimator ->
            setValueWithNoAnimation(
                valueAnimator.animatedValue as Float
            )
        }
        mAngleAnimator = ValueAnimator.ofFloat(0f, 360f)
        mAngleAnimator?.setDuration(DEFAULT_ANIMATION_TIME.toLong())
        mAngleAnimator?.addUpdateListener { valueAnimator ->
            setBaseAngle(
                valueAnimator.animatedValue as Float
            )
        }
        mAngleAnimator?.repeatCount = Animation.INFINITE
        if (mIndeterminate) {
            mAngleAnimator?.start()
        }
        super.setScaleType(SCALE_TYPE)
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType = SCALE_TYPE

    fun setOnCircularBarChangeListener(listener: OnCircularSeekBarChangeListener?) {
        onChangeListener = listener
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    override fun onDraw(canvas: Canvas) {
        if (mDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        if (mBitmap == null) {
            return
        }
        canvas.save()
        canvas.rotate(mBaseAngle, mDrawableRect.centerX(), mDrawableRect.centerY())
        if (mBorderWidth > 0) {
            mBorderPaint.color = mBorderColor
            canvas.drawArc(mBorderRect, 0f, 360f, false, mBorderPaint)
        }
        mBorderPaint.color = mProgressColor
        var sweetAngle = mProgressValue / 100 * 360
        if (mIndeterminate) {
            sweetAngle = mIndeterminateAngle
        }
        canvas.drawArc(
            mBorderRect,
            0f,
            if (mDrawAntiClockwise) -sweetAngle else sweetAngle,
            false,
            mBorderPaint
        )
        canvas.restore()
        canvas.drawCircle(
            mDrawableRect.centerX(),
            mDrawableRect.centerY(),
            mDrawableRadius,
            mBitmapPaint
        )
        if (mFillColor != Color.TRANSPARENT) {
            canvas.drawCircle(
                mDrawableRect.centerX(),
                mDrawableRect.centerY(),
                mDrawableRadius,
                mFillPaint
            )
        }
    }

    fun setValue(newValue: Float) {
        if (mIndeterminate) {
            mProgressValue = newValue
            return
        }
        if (animationState) {
            if (mValueAnimator!!.isRunning) {
                mValueAnimator!!.cancel()
            }
            mValueAnimator!!.setFloatValues(mProgressValue, newValue)
            mValueAnimator!!.start()
        } else {
            setValueWithNoAnimation(newValue, false)
        }
    }

    fun setValueWithNoAnimation(newValue: Float) {
        setValueWithNoAnimation(newValue, false)
    }

    fun setValueWithNoAnimation(newValue: Float, fromUser: Boolean) {
        onChangeListener?.onProgressChanged(this, newValue.toInt(), fromUser)
        mProgressValue = newValue
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    /**
     * Change state of progress value animation. set it to 'false' if you don't want any animation
     *
     * @param state boolean state of progress animation. if set to false, no animation happen whenever value is changed
     */
    fun setProgressAnimationState(state: Boolean) {
        animationState = state
    }

    /**
     * change interpolator of animation to get more effect on animation
     *
     * @param interpolator animation interpolator
     */
    fun setProgressAnimatorInterpolator(interpolator: TimeInterpolator?) {
        mValueAnimator!!.interpolator = interpolator
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(@ColorInt borderColor: Int) {
        if (borderColor == mBorderColor) {
            return
        }
        mBorderColor = borderColor
        mBorderPaint.color = mBorderColor
        invalidate()
    }

    fun setBorderProgressColor(@ColorInt borderColor: Int) {
        if (borderColor == mProgressColor) {
            return
        }
        mProgressColor = borderColor
        invalidate()
    }


    @Deprecated("Use {@link #setBorderColor(int)} instead")
    fun setBorderColorResource(@ColorRes borderColorRes: Int) {
        setBorderColor(context.resources.getColor(borderColorRes))
    }

    /**
     * Return the color drawn behind the circle-shaped drawable.
     *
     * @return The color drawn behind the drawable
     */
    @Deprecated("Fill color support is going to be removed in the future")
    fun getFillColor(): Int {
        return mFillColor
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColor The color to be drawn behind the drawable
     */
    @Deprecated("Fill color support is going to be removed in the future")
    fun setFillColor(@ColorInt fillColor: Int) {
        if (fillColor == mFillColor) {
            return
        }
        mFillColor = fillColor
        mFillPaint.color = fillColor
        invalidate()
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColorRes The color resource to be resolved to a color and
     * drawn behind the drawable
     */
    @Deprecated("Fill color support is going to be removed in the future")
    fun setFillColorResource(@ColorRes fillColorRes: Int) {
        setFillColor(context.resources.getColor(fillColorRes))
    }

    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        if (borderWidth == mBorderWidth) {
            return
        }
        mBorderWidth = borderWidth
        setup()
    }

    fun isBorderOverlay(): Boolean {
        return mBorderOverlay
    }

    fun setBorderOverlay(borderOverlay: Boolean) {
        if (borderOverlay == mBorderOverlay) {
            return
        }
        mBorderOverlay = borderOverlay
        setup()
    }

    fun isDisableCircularTransformation(): Boolean {
        return mDisableCircularTransformation
    }

    fun setDisableCircularTransformation(disableCircularTransformation: Boolean) {
        if (mDisableCircularTransformation == disableCircularTransformation) {
            return
        }
        mDisableCircularTransformation = disableCircularTransformation
        initializeBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf == mColorFilter) {
            return
        }
        mColorFilter = cf
        applyColorFilter()
        invalidate()
    }

    private fun applyColorFilter() {
        mBitmapPaint.setColorFilter(mColorFilter)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLOR_DRAWABLE_DIMENSION,
                    COLOR_DRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = if (mDisableCircularTransformation) {
            null
        } else {
            getBitmapFromDrawable(drawable)
        }
        setup()
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (width == 0 && height == 0) {
            return
        }
        if (mBitmap == null) {
            invalidate()
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.setShader(mBitmapShader)
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mBorderPaint.strokeCap = Paint.Cap.ROUND
        mFillPaint.style = Paint.Style.FILL
        mFillPaint.isAntiAlias = true
        mFillPaint.color = mFillColor
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        mBorderRect.set(calculateBounds())
        mBorderRadius =
            ((mBorderRect.height() - mBorderWidth) / 2.0f).coerceAtMost((mBorderRect.width() - mBorderWidth) / 2.0f)
        mDrawableRect.set(mBorderRect)
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth.toFloat(), mBorderWidth.toFloat())
        }
        mDrawableRadius = (mDrawableRect.height() / 2).coerceAtMost(mDrawableRect.width() / 2)
        if (mInnrCircleDiammeter > 1) mInnrCircleDiammeter = 1f
        mDrawableRadius *= mInnrCircleDiammeter
        applyColorFilter()
        updateShaderMatrix()
        invalidate()
    }


    private fun getMeasurementSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> defaultSize.coerceAtMost(size)
            MeasureSpec.UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getMeasurementSize(widthMeasureSpec, 600)
        val height = getMeasurementSize(heightMeasureSpec, 600)
        setMeasuredDimension(width, height)
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = availableWidth.coerceAtMost(availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f
        return RectF(
            left + getBorderWidth(),
            top + getBorderWidth(),
            left + sideLength - getBorderWidth(),
            top + sideLength - getBorderWidth()
        )
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + mDrawableRect.left,
            (dy + 0.5f).toInt() + mDrawableRect.top
        )
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }


    /**
     * ------------------------------------------
     * ------------------------------------------
     * ------------ All Things about touch ------
     * ------------------------------------------
     * ------------------------------------------
     */
    private var gestureListener: GestureDetector? = null

    fun setupGestureLitener(context: Context?) {
        gestureListener = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(motionEvent: MotionEvent): Boolean {
                if (computeInArea(motionEvent.x, motionEvent.y)) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    onChangeListener?.onClick(this@CircularMusicProgressBar)
                    postInvalidate()
                    return true
                } else if (mIndeterminate) {
                    return false
                } else if (computeAndSetAngle(motionEvent.x, motionEvent.y)) {
                    return true
                }
                return false
            }

            override fun onShowPress(motionEvent: MotionEvent) {}
            override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
                endGesture()
                return false
            }

            override fun onScroll(
                motionEvent: MotionEvent,
                motionEvent1: MotionEvent,
                v: Float,
                v1: Float
            ): Boolean {
                computeAndSetAngle(motionEvent1.x, motionEvent1.y)
                postInvalidate()
                return true
            }

            override fun onLongPress(motionEvent: MotionEvent) {
                onChangeListener?.onLongPress(this@CircularMusicProgressBar)
            }

            override fun onFling(
                motionEvent: MotionEvent,
                motionEvent1: MotionEvent,
                v: Float,
                v1: Float
            ): Boolean {
                return false
            }
        })
    }

    private fun computeInArea(x: Float, y: Float): Boolean {
        val calculatedX = x - (width / 2).toFloat()
        val calculatedY = y - (height / 2).toFloat()
        return sqrt((calculatedX * calculatedX + calculatedY * calculatedY).toDouble()) <= mDrawableRadius / 3 * 2
    }

    private fun computeAndSetAngle(x: Float, y: Float): Boolean {
        val calculatedX = x - (width / 2).toFloat()
        val calculatedY = y - (height / 2).toFloat()
        val circleDiameter = mDrawableRadius + mBorderWidth
        val radius = sqrt((calculatedX * calculatedX + calculatedY * calculatedY).toDouble())
        if (radius > circleDiameter || radius < mDrawableRadius / 3 * 2) {
            return false
        }
        var angle: Int = if (mDrawAntiClockwise) {
            (180.0 * atan2(
                calculatedX.toDouble(),
                calculatedY.toDouble()
            ) / Math.PI - mBaseAngle).toInt()
        } else {
            (180.0 * atan2(
                calculatedY.toDouble(),
                calculatedX.toDouble()
            ) / Math.PI - mBaseAngle).toInt()
        }
        angle = if (angle > 0) angle else 360 + angle
        val intoPercent = (angle * 100 / 360).toFloat()
        setValueWithNoAnimation(intoPercent, true)
        return true
    }

    private fun endGesture() {
        parent.requestDisallowInterceptTouchEvent(false)
        postInvalidate()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (gestureListener == null || !mEnableTouch) {
            false
        } else gestureListener!!.onTouchEvent(event!!)
    }

    private fun setBaseAngle(newAngle: Float) {
        mBaseAngle = newAngle
        invalidate()
    }

    fun setIndeterminate(indeterminate: Boolean) {
        if (indeterminate && mIndeterminate || !indeterminate && !mIndeterminate) {
            return
        }
        mIndeterminate = indeterminate
        if (indeterminate) {
            if (mValueAnimator!!.isRunning) {
                mValueAnimator!!.cancel()
            }
            mAngleAnimator!!.start()
        } else {
            if (mAngleAnimator!!.isRunning) {
                mAngleAnimator!!.cancel()
            }
            mBaseAngle = mBaseStartAngle
            setValueWithNoAnimation(mProgressValue)
        }
    }

    fun isIndeterminated(): Boolean {
        return mIndeterminate
    }
}
