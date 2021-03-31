package com.jeje.friendpicker.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import androidx.core.content.ContextCompat
import com.jeje.friendpicker.R
import kotlin.math.min

class SquircleImageView : androidx.appcompat.widget.AppCompatImageView {
    private var maskPaint: Paint = Paint()
    private var borderPaint: Paint = Paint()

    private val maskPath = Path()
    private val fillPath = Path()
    private val borderPath = Path()

    private var borderWidth: Float = 0.5f
    private var fillColor: Int = 0
    private var fillPaint: Paint = Paint()
    private var borderColor: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquircleBorderImageView)
            fillColor = typedArray.getColor(R.styleable.SquircleBorderImageView_squircle_fillColor, 0)
            borderColor = typedArray.getColor(R.styleable.SquircleBorderImageView_squircle_borderColor, 0)
            borderWidth = typedArray.getDimension(R.styleable.SquircleBorderImageView_squircle_borderWidth, borderWidth)
            typedArray.recycle()
        }

        if (fillColor != 0) {
            fillPaint.apply {
                style = Paint.Style.FILL
                isAntiAlias = true
                color = fillColor
            }
        }

        maskPaint.run {
            isAntiAlias = true
            color = Color.BLACK
            xfermode = PorterDuffXfermode(if (Build.VERSION.SDK_INT < 28) PorterDuff.Mode.DST_IN else PorterDuff.Mode.DST_OUT)
        }

        borderPaint.run {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = if (borderColor == 0) {
//                if (ThemeManager.getInstance()
//                                .isDarkThemeApplicable(ContextUtils.getActivity(context))
//                ) {
                    ContextCompat.getColor(context, R.color.daynight_gray150a)
//                } else {
//                    ContextCompat.getColor(context, R.color.profile_outline_mask)
//                }
            } else borderColor
//            if (DebugPref.enableBorderWidth()) {
//                color = Color.RED
//                borderWidth = 2.0f
//                alpha = 70
//            }

            val dipToPixelToFloat = (borderWidth * getDensity())
            borderPaint.strokeWidth = dipToPixelToFloat
        }
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        borderPaint.color = color
    }

    fun setFillColor(color: Int) {
        fillColor = color
        fillPaint.color = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updatePath(w, h)
    }

    private fun updatePath(width: Int, height: Int) {
        if (width == 0 || height == 0) {
            return
        }

        SquircleUtils.getSquircleProfilePath(SquircleUtils.WHOLE).let { path ->
            maskPath.run {
                reset()
                set(path)
            }
            borderPath.run {
                reset()
                set(path)
            }
            if (fillColor != 0) {
                fillPath.run {
                    reset()
                    set(path)
                }
            }
        }

        SquircleUtils.makeScale(maskPath, width.toFloat(), height.toFloat(), 0.0f)
        if (fillColor != 0) {
            SquircleUtils.makeScale(fillPath, width.toFloat(), height.toFloat(), 0.0f)
        }
        val dipToPixelToFloat = (borderWidth * getDensity())

        SquircleUtils.makeScale(borderPath, width - dipToPixelToFloat, height - dipToPixelToFloat, dipToPixelToFloat / 2f)
        if (Build.VERSION.SDK_INT >= 28) {
            maskPath.fillType = Path.FillType.EVEN_ODD
            maskPath.addRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), Path.Direction.CW)
        }

    }

    fun getDensity(): Float {
        return context.resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        if (fillColor != 0) {
            canvas.drawPath(fillPath, fillPaint)
        }

        val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)

        super.onDraw(canvas)

        canvas.drawPath(maskPath, maskPaint)
        canvas.restoreToCount(sc)

        canvas.drawPath(borderPath, borderPaint)
    }
}

object SquircleUtils {
    const val WHOLE = 0
    const val LEFT_TOP_2 = 1
    const val LEFT_BOTTOM_3 = 2
    const val RIGHT_BOTTOM_3 = 3
    const val RIGHT_BEHIND = 4
    private val factory: SparseArray<Path> = SparseArray()
//        M5.029533618,29.6605584
//        C7.85517415,27.0235225
//        9,22.6598324
//        9,16
//
//        C9,9.34016755
//        7.85517415,4.97647745
//        5.029533618,2.33944156
//
//        C7.543037,0.695257893
//        11.11530317,0
//        16,0
//
//        C27.8844444,0
//        32,4.11555556
//        32,16
//
//        C32,27.8844444
//        27.8844444,32
//        16,32
//
//        C11.11530317,32
//        7.543037,31.3047421
//        5.029533618,29.6605584
    //"M383.816,206.345 C270.335,215.99, 215.891,271.217, 206.341,383.816 C201.668,383.939, 196.888,383.999, 192,383.999 C50.667,383.999, 0,333.332, 0,191.999 C0,55.999, 50.6667,-0.001, 192,-0.001 C333.333,-0.001, 384,50.6657, 384,191.999 C384,196.889, 383.939,201.67, 383.816,206.344 Z"

    private val POINTS_RIGHT_BEHIND = arrayOf(
            PointF(5.029533618F,29.6605584F),

            PointF(7.85517415F,27.0235225F),
            PointF(9F,22.6598324F),
            PointF(9F,16F),

            PointF(9F,9.34016755F),
            PointF(7.85517415F,4.97647745F),
            PointF(5.029533618F,2.33944156F),

            PointF(7.543037F,0.695257893F),
            PointF(11.11530317F,0F),
            PointF(16f, 0F),

            PointF(27.8844444F,0F),
            PointF(32F,4.11555556F),
            PointF(32F,16F),

            PointF(32F,27.8844444F),
            PointF(27.8844444F,32F),
            PointF(16F,32F),

            PointF(11.11530317F,32F),
            PointF(7.543037F,31.3047421F),
            PointF(5.029533618F,29.6605584F))

    private val POINT_2_UP = arrayOf(
            PointF(383.816F, 206.345F),

            PointF(270.335F, 215.99F),
            PointF(215.891F, 271.217F),
            PointF(206.341F, 383.816F),

            PointF(201.668F, 383.939F),
            PointF(196.888F, 383.999F),
            PointF(192F, 383.999F),

            PointF(50.667F, 383.999F),
            PointF(0F, 333.332F),
            PointF(0F, 191.999F),

            PointF(0F, 55.999F),
            PointF(50.6667F, -0.001F),
            PointF(192F, -0.001F),

            PointF(333.333F, -0.001F),
            PointF(384F, 50.6657F),
            PointF(384F, 191.999F),

            PointF(384F, 196.889F),
            PointF(383.939F, 201.67F),
            PointF(383.816F, 206.344F))

    // 3_left_bottom M189.123,0 C221.469,38.9616, 273.006,58.1043, 347.337,61.0344 C330.268,94.6682, 322.234,138.05, 322.234,191.997 C322.234,250.41, 330.997,296.041, 350,330.369 C320.604,368.401, 270.148,384.001, 191.806,384.001 C50.616,384.001, 0,333.333, 0,191.998 C0,56.86, 49.9738,0.71, 189.123,0.001 Z
    private val POINTS_3_LEFT_BOTTOM = arrayOf(
            PointF(189.123F, 0F), // M

            PointF(221.469F, 38.9616F),    // C
            PointF(273.006F, 58.1043F),
            PointF(347.337F, 61.0344F),

            PointF(330.268F, 94.6682F),     // C
            PointF(322.234F, 138.05F),
            PointF(322.234F, 191.997F),

            PointF(322.234F, 250.41F),       // C
            PointF(330.997F, 296.041F),
            PointF(350F, 330.369F),

            PointF(320.604F, 368.401F),      // C
            PointF(270.148F, 384.001F),
            PointF(191.806F, 384.001F),

            PointF(50.616F, 384.001F),       // C
            PointF(0F, 333.333F),
            PointF(0F, 191.998F),

            PointF(0F, 56.86F),      //C
            PointF(49.9738F, 0.71F),
            PointF(189.123F, 0.001F)    //Z
    )
    private val EXTRA = arrayOf(
            PointF(384F,191.997F)
    )

    // 3_right_bottom M194.686,0 C333.976,0.642036, 384,51.5603, 384,191.997 C384,333.333, 333.333,384, 192,384 C50.667,384, 0,333.333, 0,191.997 C0,134.912, 8.92644,91.921, 29.7373,61.25 C107.69,59.2053, 161.362,40.0991, 194.686,-0.0002 Z
    private val POINTS_3_RIGHT_BOTTOM = arrayOf(
            PointF(194.686F, 0F),      // M
            PointF(333.976F, 0.642036F),   // C
            PointF(384F, 51.5603F),
            PointF(384F, 191.997F),
            PointF(384F, 333.333F),        // C
            PointF(333.333F, 384F),
            PointF(192F, 384F),
            PointF(50.667F, 384F),     //  C
            PointF(0F, 333.333F),
            PointF(0F, 191.997F),
            PointF(0F, 134.912F),      //C
            PointF(8.92644F, 91.921F),
            PointF(29.7373F, 61.25F),
            PointF(107.69F, 59.2053F),     //C
            PointF(161.362F, 40.0991F),
            PointF(194.686F, -0.0002F)     // M
    )

    // M270,540 C71.251,540,0,468.754,0,270 C0,78.748,71.2511,0,270,0 C468.754,0,540,71.2511,540,270 C540,468.754,468.754,540,270,540 Z
    private val POINTS_WHOLE = arrayOf(
            PointF(270f, 540f),

            PointF(71.251f, 540f),
            PointF(0f, 468.754f),
            PointF(0f, 270f),

            PointF(0f, 71.2511f),
            PointF(71.2511f, 0f),
            PointF(270f, 0f),

            PointF(468.754f, 0f),
            PointF(540f, 71.2511f),
            PointF(540f, 270f),

            PointF(540f, 468.754f),
            PointF(468.754f, 540f),
            PointF(270f, 540f)
    )

    @JvmStatic
    fun makeScale(path: Path, width: Float, height: Float, extraScale: Float): Path {
        val bound = RectF()
        val matrix = Matrix()
        val scale = min(width, height)

        path.computeBounds(bound, true)
        val sx = (scale / bound.width()) * 100 / 100f
        val sy = (scale / bound.height()) * 100 / 100f
        matrix.setScale(sx, sy)
        matrix.postTranslate(extraScale, extraScale)
        path.transform(matrix)
        return path
    }

    /**
     * Squircle 모양을 원할때
     * {@link #POINT_WHOLE}
     */
    @JvmStatic
    fun getScaledPath(width: Int, height: Int): Path {
        val matrix = Matrix()
        val scale = Math.min(width, height)
        val bound = RectF()

        val path = Path()
        path.set(getSquircleProfilePath(WHOLE))
        path.computeBounds(bound, true)
        matrix.setScale(scale / bound.width(), scale/ bound.height())

        path.close()
        path.transform(matrix)
        return path
    }

    /**
     * 모양에 맞는 프로필용 squirclePath를 반환
     */
    @JvmStatic
    fun getSquircleProfilePath(type: Int): Path {
        return factory.getOrPut(type) { pathOf(type) }
    }

    private fun pathOf(type: Int): Path {
        val pathPoint: Array<PointF> = when (type) {
            LEFT_TOP_2 -> POINT_2_UP
            LEFT_BOTTOM_3 -> POINTS_3_LEFT_BOTTOM
            RIGHT_BOTTOM_3 -> POINTS_3_RIGHT_BOTTOM
            RIGHT_BEHIND -> POINTS_RIGHT_BEHIND
            else -> POINTS_WHOLE
        }

        return Path().apply {
            moveTo(pathPoint[0].x, pathPoint[0].y)
            for (i in 1..pathPoint.lastIndex step 3) {
                val c1 = pathPoint[i]
                val c2 = pathPoint[i + 1]
                val end = pathPoint[i + 2]
                cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
            }
            // path를 그대로 그리면 오른쪽 빈 공간을 그려낼 수 없기 때문에 의미없는 점을 하나 찍는다.
            // ㅁ
            //ㅁㅁ 요런 형태의 좌하단만 영향이 있음
            if (type == LEFT_BOTTOM_3) {
                moveTo(EXTRA[0].x, EXTRA[0].y)
                lineTo(EXTRA[0].x, EXTRA[0].y)
            }
            // ㅁ 왼쪽이 비었기 때문에 공간을 확보해줘야함
            if (type == RIGHT_BEHIND) {
                moveTo(0F, 16F)
                lineTo(0F, 16F)
            }
            close()
        }
    }
}


inline fun <T> SparseArray<T>.getOrPut(key: Int, defaultValue: () -> T): T {
    return get(key) ?: defaultValue().also { put(key, it) }
}


