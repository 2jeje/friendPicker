package com.jeje.friendpicker.view/*package com.jeje.friendpicker

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.Comparator
import kotlin.math.abs
import kotlin.math.max

class SideIndexView : View {

    companion object {
        private const val FAVORITE = "!"
        private const val RECOMMEND = "+"
        private const val ETC = "#"
    }

    private val indexItems = ArrayList<String>()
    private var portraitIndexItems: Array<String>
    private var landscapeIndexItems: Array<String>

    private var currentIndex = -1

    private var currentY = -1f

    private val textPaint: Paint = Paint()
    private var textColor: Int = Color.GRAY
    private var indexTextSize: Float = 0.toFloat()

    private var viewHeight: Float = 0.toFloat()

    private var indexItemWidth: Float = 0.toFloat()
    private var indexItemHeight: Float = 0.toFloat()
    private var firstIndexItemY: Float = 0.toFloat()
    private var firstDrawableIndexItemY: Float = 0.toFloat()

    private var favoriteDrawable: Drawable?

    private var touched = false

    var recyclerView: RecyclerView? = null

    private var groupIndexMap: TreeMap<String, Int>? = null
    private var favoriteOffset = -1
    private var recommendOffset = -1
    private var friendListOffset: Int = 0
    private var friendListSize: Int = 0

    private var isShowSingleToast: Boolean = false

    private var listScrolling = false
    private var scrolling = false
    private val scrollListener = OnScrollListener()
    val onScrollListener: RecyclerView.OnScrollListener
        get() = scrollListener

    var isEnabledSideIndex = true

    private var toastTextView: TextView
    private var toastImageView: ImageView

    interface OnTouchListener {
        fun onTouchDown()

        fun onTouchUp()
    }
    interface OnVisibleChangeListener {
        fun onChange(visibility: Int)
    }
    var onTouchListener: OnTouchListener? = null
    var onVisibleChangeListener: OnVisibleChangeListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SideIndexView).run {
            textColor = getColor(R.styleable.SideIndexView_sideindex_text_color, Color.GRAY)
            indexTextSize = getDimension(R.styleable.SideIndexView_sideindex_text_size, ViewUtils.spToPixel(context, 12f).toFloat())
            recycle()

        }

        textPaint.run {
            isAntiAlias = true
            color = textColor
            textSize = indexTextSize
            textAlign = Paint.Align.CENTER
            try {
                typeface = Typeface.SANS_SERIF
            } catch (ignore: Exception) {
            }
        }

        resources.run {
            landscapeIndexItems = getStringArray(R.array.side_indexer_landscape)
            portraitIndexItems = getStringArray(R.array.side_indexer)
            favoriteDrawable = ContextCompat.getDrawable(context, R.drawable.list_index_ico_favorite)?.apply {
                DrawableUtils.applyTint(this, ContextCompat.getColor(context, R.color.daynight_gray600s))
            }
            setIternalIndexItems(configuration.orientation)
        }

        singleToast.initializeToast(context, R.layout.expandable_list_position)

        toastTextView = singleToast.findViewById(R.id.text) as TextView
        toastImageView = singleToast.findViewById(R.id.icon) as ImageView
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val fontMetrics = textPaint.fontMetrics
        viewHeight = (height - paddingTop - paddingBottom).toFloat()
        indexItemHeight = viewHeight / indexItems.size

        for (indexItem in indexItems) {
            indexItemWidth = max(indexItemWidth, textPaint.measureText(indexItem))
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec((indexItemWidth + paddingLeft + paddingRight).toInt(), MeasureSpec.EXACTLY), heightMeasureSpec)

        firstIndexItemY = (height / 2 - indexItems.size * indexItemHeight / 2) + (indexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2) - fontMetrics.ascent

        firstDrawableIndexItemY = (paddingTop + (indexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in indexItems.indices) {
            val baseLineY = firstIndexItemY + indexItemHeight * i
            val scale = calcItemScale(i)
            textPaint.let { paint ->
                paint.alpha = if (i == currentIndex) 255 else (255 * (1 - scale)).toInt()
                paint.textSize = indexTextSize + indexTextSize * scale * 0.5f
            }
            val baseLineX = paddingLeft + indexItemWidth / 2

            if (indexItems[i] == FAVORITE) {
                favoriteDrawable?.run {
                    val top = (firstDrawableIndexItemY + indexItemHeight * i).toInt()
                    setBounds(paddingLeft, top, paddingLeft + indexItemWidth.toInt(), top + indexItemWidth.toInt())
                    draw(canvas)
                }
            } else {
                canvas.drawText(indexItems[i], baseLineX, baseLineY, textPaint)
            }
        }

        textPaint.run {
            alpha = 255
            textSize = indexTextSize
        }
    }

    private fun calcItemScale(index: Int): Float {
        var scale = 0f
        if (currentIndex != -1) {
            val distance = abs(currentY - (indexItemHeight * index + indexItemHeight / 2)) / indexItemHeight
            scale = max(1 - distance * distance / 16, 0f)
        }
        return scale
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (indexItems.isEmpty() || visibility != VISIBLE) {
            return super.onTouchEvent(event)
        }

        currentIndex = getSelectedIndex(event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touched = true
                invalidate()
                this.onTouchListener?.onTouchDown()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                scrolling = true
                if (touched) {
                    scrollToPosition(indexItems[currentIndex])
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentIndex = -1
                touched = false
                invalidate()
                this.onTouchListener?.onTouchUp()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun getSelectedIndex(eventY: Float): Int {
        currentY = eventY - (height / 2 - viewHeight / 2)
        if (currentY <= 0) {
            return 0
        }
        var index = (currentY / indexItemHeight).toInt()
        if (index >= indexItems.size) {
            index = indexItems.size - 1
        }
        return index
    }

    private fun scrollToPosition(index: String) {
        val position = getHigherValue(groupIndexMap, index)

        if (index == FAVORITE) {
            (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(favoriteOffset, 0)
        } else if (index == RECOMMEND) {
            (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(recommendOffset, 0)
        } else if (position == null) {
            recyclerView?.let { recyclerView ->
                recyclerView.adapter?.let { adapter ->
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        } else {
            if (recyclerView?.layoutManager is LinearLayoutManager) {
                (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position + friendListOffset, 0)
            } else {
                recyclerView?.scrollToPosition(position + friendListOffset)
            }
        }
        if (index == FAVORITE) {
            toastImageView.apply {
                setImageDrawable(DrawableUtils.applyTint(ContextCompat.getDrawable(context, R.drawable.list_index_ico_favorite_large),
                        ContextCompat.getColor(context, R.color.daynight_gray600s)))
            }.visibility = VISIBLE
            toastTextView.visibility = GONE
        } else {
            toastTextView.apply {
                text = index
            }.visibility = VISIBLE
            toastImageView.visibility = GONE
        }

        isShowSingleToast = true
        singleToast.setVisibility(VISIBLE)

        singleToast.removeToast(1500)
    }

    fun setConfigurationChanged(configuration: Configuration) {
        setIternalIndexItems(configuration.orientation)
        requestLayout()
    }

    fun setIndexItems(sideIndexRes: Int, sideLandscapeIndexRes: Int) {
        portraitIndexItems = context.resources.getStringArray(sideIndexRes)
        landscapeIndexItems = context.resources.getStringArray(sideLandscapeIndexRes)
        setIternalIndexItems(context.resources.configuration.orientation)
        requestLayout()
    }

    private fun setIternalIndexItems(orientation: Int) {
        indexItems.clear()
        if (favoriteOffset != -1) {
            indexItems.add(FAVORITE)
        }

        if (recommendOffset != -1) {
            indexItems.add(RECOMMEND)
        }

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            for (portraitIndexItem in portraitIndexItems) {
                indexItems.add(portraitIndexItem)
            }
        } else {
            for (landscapeIndexItem in landscapeIndexItems) {
                indexItems.add(landscapeIndexItem)
            }
        }
    }

    fun setTextColor(color: Int) {
        textColor = color
        textPaint.color = color
        invalidate()
    }

    fun setTextSize(size: Float) {
        if (indexTextSize == size) {
            return
        }
        indexTextSize = size
        textPaint.textSize = size
        invalidate()
    }

    fun setDataSource(friendList: List<*>, friendListOffset: Int, favoriteOffset: Int, recommendOffset: Int) {
        this.friendListSize = friendList.size
        this.friendListOffset = friendListOffset
        this.favoriteOffset = favoriteOffset
        this.recommendOffset = recommendOffset

        groupIndexMap = try {
            getGroupIndexMap(friendList, sideIndexerComparator)
        } catch (e: Exception) {
            getGroupIndexMap(friendList, sideIndexerOldComparator)
        }

        invalidate()
    }


    override fun dispatchVisibilityChanged(changedView: View, visibility: Int) {
        super.dispatchVisibilityChanged(changedView, visibility)
        onVisibleChangeListener?.onChange(visibility)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        singleToast.destroy()
    }

    override fun setVisibility(visibility: Int) {
        setVisibility(visibility, true)
    }

    fun setVisibility(visibility: Int, usingAnim: Boolean) {
        if (usingAnim) {
            var anim: Animation? = null
            when (visibility) {
                INVISIBLE, GONE -> anim = AnimationUtils.loadAnimation(context, R.anim.fade_out_short)
                VISIBLE -> anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_short)
            }
            this.animation = anim
            this.startAnimation(anim)
        } else {
            singleToast.removeToast(0)
        }

        if (visibility != VISIBLE) {
            currentIndex = -1
        }
        super.setVisibility(visibility)
    }

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {
        internal var scrollState = RecyclerView.SCROLL_STATE_IDLE
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (isEnabledSideIndex && !needScroll() && !listScrolling) {
                return
            }
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> if (!listScrolling && scrollState != newState && friendListSize > 0) {
                    listScrolling = true
                    visibility = VISIBLE
                }
                RecyclerView.SCROLL_STATE_IDLE -> if (listScrolling && !scrolling) {
                    postDelayed({
                        if (scrolling) {
                            return@postDelayed
                        }
                        listScrolling = false
                        if (visibility >= INVISIBLE) {
                            return@postDelayed
                        }
                        visibility = INVISIBLE
                    }, 1000)
                }
            }
            scrollState = newState
        }
    }

    private fun needScroll() : Boolean = recyclerView?.run {
        computeVerticalScrollExtent() < computeVerticalScrollRange()
    } ?: false

    private val singleToast = object : SingleToast() {

        override fun removeWindow() {
            if (isShowSingleToast) {
                isShowSingleToast = false
                scrolling = false
                this.setVisibility(INVISIBLE)
                scrollListener.onScrollStateChanged(recyclerView!!, AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
            }
        }
    }

    private fun getGroupIndexMap(friendList: List<*>, comparator: java.util.Comparator<String>): TreeMap<String, Int> {
        val alphabetIndexSet: TreeSet<String> by lazy {
            val alphabetIndexSet = TreeSet(comparator)
            for (alphabet in 'a'..'z') {
                alphabetIndexSet.add(alphabet.toString())
            }
            alphabetIndexSet
        }
        val index = TreeMap<String, Int>(comparator)
        var prevChar: Char = 0.toChar()
        for (i in friendList.indices) {
            val o = friendList[i]

            val curChar: Char = if (o is FilterSearchable) {
                PhonemeUtils.getFirstPhoneme(o.filterKeyword.trimZeroWidthChars())
            } else {
                prevChar
            }

            if (prevChar == curChar) {
                continue
            }

            val groupName = curChar.toString()
            if (groupName.length > 1) {
                continue
            }

            val key = when (PhonemeUtils.startsWith(curChar)) {
                PhonemeUtils.ALPHABET -> {
                    try {
                        alphabetIndexSet.floor(curChar.toString()).toUpperCase(Locale.ENGLISH)
                    } catch (e: Exception) {
                        groupName.toUpperCase(FriendManager.comparator.locale())
                    }
                }
                PhonemeUtils.UNKNOWN -> {
                    ETC
                }
                else -> {
                    groupName.toUpperCase(FriendManager.comparator.locale())
                }
            }

            if (!index.containsKey(key)) {
                index[key] = i
            }
            prevChar = curChar
        }

        return index
    }

    private val sideIndexerComparator: java.util.Comparator<String> =
            Comparator { lhs, rhs -> FriendManager.compare(lhs.trimZeroWidthChars(), rhs.trimZeroWidthChars()) }

    private val sideIndexerOldComparator: java.util.Comparator<String> by lazy {
        Comparator<String> { lhs, rhs -> FriendManager.compareForOld(lhs, rhs) }
    }

    private fun <K, V> getHigherValue(map: TreeMap<K, V>?, key: K?): V? {
        if (map == null || key == null) {
            return null
        }
        try {
            val firstKey = map.firstKey()
            val lastKey = map.lastKey()

            if (lastKey == key) {
                return map[lastKey]
            }

            if (firstKey == key) {
                return map[firstKey]
            }

            val sortedMap = map.tailMap(key)

            return if (sortedMap.size == 0) {
                null
            } else sortedMap[sortedMap.firstKey()]

        } catch (nse: NoSuchElementException) {
            Logger.wt(Logger.Tag.EXPANDABLE, "", nse)
        }

        return null
    }
}
 */