package com.jeje.friendpicker.util

import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import java.util.ArrayList
import kotlin.math.abs

object ViewUtils {
    private var lastGentleClickTime: Long = 0
    const val GENTLE_CLICK_INTERVAL: Long = 1000
    private var lastViewHashCode : Int? = null

    /**
     * 중복 클릭 방지 interval 내에 두번 클릭 막음
     * @param interval : 중복 클릭을 무시할 클릭 간격 시간 (ms)
     * @return : true or false
     */
    @JvmStatic
    fun isGentleClick(interval: Long): Boolean {
        val now = System.currentTimeMillis()
        if (abs(now - lastGentleClickTime) < interval) {
            // 마지막 클릭한고 얼마 안됐어! 이 클릭은 무시해~
            return false
        }
        lastGentleClickTime = now
        // 마지막 클릭하고 충분한 시간이 흘렀으니... 이제 클릭 처리해도 좋아...
        return true
    }
    @JvmStatic
    fun isGentleClick(interval: Long, viewHashCode: Int? = null): Boolean {
        val now = System.currentTimeMillis()
        if (abs(now - lastGentleClickTime) < interval && this.lastViewHashCode == viewHashCode) {
            // 마지막 클릭한고 얼마 안됐어! 이 클릭은 무시해~
            return false
        }
        lastViewHashCode = viewHashCode
        lastGentleClickTime = now
        // 마지막 클릭하고 충분한 시간이 흘렀으니... 이제 클릭 처리해도 좋아...
        return true
    }

    fun resetGentleClickInterval() {
        lastGentleClickTime = 0
    }

    @JvmStatic
    val isGentleClick: Boolean
        get() = isGentleClick(GENTLE_CLICK_INTERVAL)

    @JvmStatic
    fun dipToPixel(context: Context, dip: Float): Int {
        return unitValueToPixel(context, TypedValue.COMPLEX_UNIT_DIP, dip)
    }

    @JvmStatic
    fun spToPixel(context: Context, sp: Float): Int {
        return unitValueToPixel(context, TypedValue.COMPLEX_UNIT_SP, sp)
    }

    private fun unitValueToPixel(context: Context, unit: Int, unitValue: Float): Int {
        return TypedValue.applyDimension(unit, unitValue, context.resources.displayMetrics).toInt()
    }

    @JvmStatic
    fun <V : View?> setGone(view: V?, gone: Boolean): V? {
        view?.visibility = if (gone) View.GONE else View.VISIBLE
        return view
    }

    @JvmStatic
    fun <V : View?> setInvisible(view: V?, invisible: Boolean): V? {
        view?.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
        return view
    }

    @JvmStatic
    fun isVisible(view: View?): Boolean {
        return view != null && view.visibility == View.VISIBLE
    }

    @JvmStatic
    fun dipToPixelByResource(context: Context, resId: Int): Int {
        return context.applicationContext.resources.getDimensionPixelSize(resId)
    }

    fun getAllChildrenBFS(view: View): List<View> {
        val visited: MutableList<View> = ArrayList()
        val unvisited: MutableList<View> = ArrayList()
        unvisited.add(view)
        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)
            if (child !is ViewGroup) {
                continue
            }
            val childCount = child.childCount
            for (i in 0 until childCount) unvisited.add(child.getChildAt(i))
        }
        return visited
    }

    @JvmStatic
    fun dimBehind(popupWindow: PopupWindow, dimAmount: Float) {
        val container: View = if (popupWindow.background == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.contentView.parent as View
            } else {
                popupWindow.contentView
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.contentView.parent.parent as View
            } else {
                popupWindow.contentView.parent as View
            }
        }
        (container.layoutParams as WindowManager.LayoutParams).apply {
            this.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            this.dimAmount = dimAmount
        }.run {
            val context = popupWindow.contentView.context
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).updateViewLayout(container, this)
        }
    }

    fun drawUnderLine(textView: TextView) {
        textView.text = SpannableString(textView.text).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
    }

    fun removeUnderLine(textView: TextView) {
        textView.text = SpannableString(textView.text).apply {
            val underlineSpans = getSpans(0, length, UnderlineSpan::class.java)
            for (us in underlineSpans) {
                removeSpan(us)
            }
        }
    }

}
