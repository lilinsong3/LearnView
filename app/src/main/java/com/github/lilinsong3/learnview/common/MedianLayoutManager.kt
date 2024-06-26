package com.github.lilinsong3.learnview.common

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import kotlin.math.abs

class MedianLayoutManager(@RecyclerView.Orientation orientation: Int = DEFAULT_ORIENTATION) : RecyclerView.LayoutManager() {

    private var mOrientationHelper: OrientationHelper = OrientationHelper.createOrientationHelper(this, DEFAULT_ORIENTATION)

    private var mLayoutState: LayoutState? = null
    private val medianSnapHelper = LinearSnapHelper()

    companion object {
        private const val DEFAULT_ORIENTATION = VERTICAL
    }

    enum class LayoutDirection(val v: Int) {
        TOWARDS_START(-1),
        TOWARDS_END(1)
    }

    @RecyclerView.Orientation
    public var mOrientation = DEFAULT_ORIENTATION
        set(orientation) {
            require(orientation == LinearLayoutManager.HORIZONTAL || orientation == LinearLayoutManager.VERTICAL) { "invalid orientation:$orientation" }

            assertNotInLayoutOrScroll("In Layout Or Scroll")
            if (orientation != mOrientation) {
                mOrientationHelper = OrientationHelper.createOrientationHelper(this, orientation)
                field = orientation
                requestLayout()
            }
        }

    init {
        mOrientation = orientation
    }

    // 给xml用
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : this(getProperties(context, attrs, defStyleAttr, defStyleRes).orientation)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean = mOrientation == RecyclerView.HORIZONTAL

    override fun canScrollVertically(): Boolean = mOrientation == VERTICAL

    override fun isAutoMeasureEnabled(): Boolean = true

    override fun scrollToPosition(position: Int) {
        val scrollPosition = if (position < 0) 0 else if (position >= itemCount) itemCount - 1 else position
        mLayoutState = LayoutState(scrollPosition)
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: State?,
        position: Int
    ) {
        val medianSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int
            ): Int {
                val viewMedian = viewStart + (viewEnd - viewStart) / 2
                val boxMedian = boxStart + (boxEnd - boxStart) / 2
                return boxMedian - viewMedian
            }
        }
        medianSmoothScroller.targetPosition = position
        startSmoothScroll(medianSmoothScroller)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        medianSnapHelper.attachToRecyclerView(view)
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: Recycler?) {
        medianSnapHelper.attachToRecyclerView(null)
        super.onDetachedFromWindow(view, recycler)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: State) {
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }

        detachAndScrapAttachedViews(recycler)

        if (state.itemCount <= 0) return

        if (mLayoutState == null || mLayoutState!!.mMedianPosition == RecyclerView.NO_POSITION) {
            mLayoutState = LayoutState(if (state.hasTargetScrollPosition()) state.targetScrollPosition else 0)
        }

        val medianChild = layoutMedianChild(recycler, state)
        if (medianChild != null) {
            val layoutStartOffset = mOrientationHelper.getDecoratedStart(medianChild)
            layoutStart(recycler, state, layoutStartOffset)
            val layoutEndOffset = mOrientationHelper.getDecoratedEnd(medianChild)
            layoutEnd(recycler, state, layoutEndOffset)
        }
    }

    private fun layoutMedianChild(recycler: RecyclerView.Recycler, state: State): View? {
        // vertical layout only for now
        if (mLayoutState == null || mLayoutState!!.mMedianPosition !in 0 until state.itemCount) return null

        val halfSpace = mOrientationHelper.totalSpace / 2
        val layoutPosition = mLayoutState!!.mMedianPosition
        val medianChild = recycler.getViewForPosition(layoutPosition)

        addView(medianChild, 0)
        measureChildWithMargins(medianChild, 0, 0)

        // mOrientation方向上的Child的一半长度
        val halfMedianChildLength = mOrientationHelper.getDecoratedMeasurement(medianChild) / 2
        // 垂直于mOrientation方向上的Child长度
        val medianChildPerpendicularLength = mOrientationHelper.getDecoratedMeasurementInOther(medianChild)
        // mOrientation方向上的Child的start
        val medianChildStart = halfSpace - halfMedianChildLength
        // mOrientation方向上的Child的end
        val medianChildEnd = halfSpace + halfMedianChildLength
        // 垂直于mOrientation方向上的Child的start
        val medianChildPerpendicularStart = if (mOrientation == DEFAULT_ORIENTATION) paddingLeft else paddingTop
        // 垂直于mOrientation方向上的Child的end
        val medianChildPerpendicularEnd = medianChildPerpendicularStart + medianChildPerpendicularLength

        val left = if (mOrientation == DEFAULT_ORIENTATION) medianChildPerpendicularStart else medianChildStart
        val top = if (mOrientation == DEFAULT_ORIENTATION) medianChildStart else medianChildPerpendicularStart
        val right = if (mOrientation == DEFAULT_ORIENTATION) medianChildPerpendicularEnd else medianChildEnd
        val bottom = if (mOrientation == DEFAULT_ORIENTATION) medianChildEnd else medianChildPerpendicularEnd

        layoutDecoratedWithMargins(medianChild, left, top, right, bottom)

        return medianChild
    }

    private fun layoutStart(recycler: RecyclerView.Recycler, state: State, startOffset: Int) {
        if (mLayoutState == null || mLayoutState!!.mMedianPosition >= state.itemCount) return
        if (startOffset > mOrientationHelper.totalSpace) return

        var layoutPosition = mLayoutState!!.mMedianPosition - 1
        var layoutOffset = startOffset
        while (layoutOffset > 0 && -1 < layoutPosition) {
            val used = layoutChild(recycler, state, layoutPosition, layoutOffset, LayoutDirection.TOWARDS_START)
            layoutOffset -= used
            layoutPosition--
        }
    }

    private fun layoutEnd(recycler: RecyclerView.Recycler, state: State, startOffset: Int) {
        if (mLayoutState == null || mLayoutState!!.mMedianPosition < 0) return
        if (startOffset < 0) return

        var layoutPosition = mLayoutState!!.mMedianPosition + 1
        var layoutOffset = startOffset
        while (layoutOffset < mOrientationHelper.totalSpace && layoutPosition < state.itemCount) {
            val used = layoutChild(recycler, state, layoutPosition, layoutOffset, LayoutDirection.TOWARDS_END)
            layoutOffset += used
            layoutPosition++
        }
    }

    private fun layoutChild(
        recycler: RecyclerView.Recycler,
        state: State,
        layoutPosition: Int,
        startOffset: Int,
        layoutDirection: LayoutDirection
    ): Int {
        if (layoutPosition !in 0 until state.itemCount) return 0
        val layoutChild = recycler.getViewForPosition(layoutPosition)
        if (layoutDirection == LayoutDirection.TOWARDS_END) {
            addView(layoutChild)
        } else {
            addView(layoutChild, 0)
        }
        measureChildWithMargins(layoutChild, 0, 0)

        // mOrientation方向上的Child的长度
        val length = mOrientationHelper.getDecoratedMeasurement(layoutChild)
        // 垂直于mOrientation方向上的Child长度
        val perpendicularLength = mOrientationHelper.getDecoratedMeasurementInOther(layoutChild)
        // mOrientation方向上的Child的start
        val start = if (layoutDirection == LayoutDirection.TOWARDS_END) startOffset else startOffset - length
        // 垂直于mOrientation方向上的Child的start
        val perpendicularStart =
            if (mOrientation == DEFAULT_ORIENTATION) paddingLeft else paddingTop
        // mOrientation方向上的Child的end
        val end = start + length
        // 垂直于mOrientation方向上的Child的end
        val perpendicularEnd = perpendicularStart + perpendicularLength

        val left = if (mOrientation == DEFAULT_ORIENTATION) perpendicularStart else start
        val top = if (mOrientation == DEFAULT_ORIENTATION) start else perpendicularStart
        val right = if (mOrientation == DEFAULT_ORIENTATION) perpendicularEnd else end
        val bottom = if (mOrientation == DEFAULT_ORIENTATION) end else perpendicularEnd

        layoutDecoratedWithMargins(layoutChild, left, top, right, bottom)

        return length
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: State?) =
        scrollBy(dx, recycler, state)

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: State?) =
        scrollBy(dy, recycler, state)

    private fun getEdgeChild(layoutDirection: LayoutDirection) = getChildAt(if (layoutDirection == LayoutDirection.TOWARDS_END) childCount - 1 else 0)

    private fun scrollBy(
        delta: Int,
        recycler: RecyclerView.Recycler?,
        state: State?
    ): Int {
        if (recycler == null || state == null || childCount == 0) return 0
        // dy > 0, finger swipes from end to start; dy < 0, fingers swipes from start to end
        val scrollTowardsStart = delta > 0
        val layoutDirection =
            if (scrollTowardsStart) LayoutDirection.TOWARDS_END else LayoutDirection.TOWARDS_START

        val edgeChild = getEdgeChild(layoutDirection) ?: return 0
        val invisiblePartOfEdgeChild = if (scrollTowardsStart) {
            mOrientationHelper.getDecoratedEnd(edgeChild) - mOrientationHelper.endAfterPadding
        } else {
            -mOrientationHelper.getDecoratedStart(edgeChild)
        }.coerceAtLeast(0)
        // 为了让新布局进来的ChildView可以被滑动到可见区域，先用滑动距离减去边缘ChildView的隐藏部分
        // 如果不减去边缘ChildView的隐藏部分，而将滑动产生的空间直接用来布局新ChildView，布局会混乱，不符合预期结果
        val availableSpace = (abs(delta) - invisiblePartOfEdgeChild).coerceAtLeast(0)
        layoutChildrenForScrolling(recycler, state, availableSpace, layoutDirection)

        val offsetDelta = calculateActualDelta(delta, layoutDirection)
        if (offsetDelta == 0) return 0

        // param amount < 0, offset children towards the start; param amount > 0, offset children towards the end
        mOrientationHelper.offsetChildren(offsetDelta)
        recycleInvisibleChildren(recycler)
        saveMedianChild()

        return -offsetDelta
    }

    private fun calculateActualDelta(delta: Int, layoutDirection: LayoutDirection): Int {
        // 初始化为滑动产生的平移量
        var scrolledDelta = -delta
        // rv中线坐标
        val rvMedianOffset = mOrientationHelper.totalSpace / 2
        // 新的边缘ChildView
        val newEdgeChild = getEdgeChild(layoutDirection) ?: return 0
        // 边缘ChildView中线坐标为：
        val edgeChildMedianOffset = mOrientationHelper.getDecoratedStart(newEdgeChild) + mOrientationHelper.getDecoratedMeasurement(newEdgeChild) / 2
        // 经过滑动平移后，边缘ChildView的中线坐标为：
        val scrolledEdgeChildMedianOffset = edgeChildMedianOffset + scrolledDelta
        // 假设继续平移至rv中线，所需要的平移量为：
        val movedToMedianOffsetDelta = rvMedianOffset - scrolledEdgeChildMedianOffset
        // 若平移至rv中线所需的平移量与滑动产生的平移量的乘积小于0，则说明两次平移的方向不一致，即第一滑动平移已经超过了rv中线，第二次平移要回到rv中线的话，需要往相反方向平移
        // 而平移最多只能平移到rv中线，因此，两次平移方向不一致的话，最终平移量需要回调
        if (movedToMedianOffsetDelta * scrolledDelta < 0) scrolledDelta += movedToMedianOffsetDelta
        return scrolledDelta
    }

    private fun recycleInvisibleChildren(recycler: Recycler) {
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            if (isInvisible(child)) {
                removeAndRecycleView(child, recycler)
            }
        }
    }

    private fun isInvisible(child: View): Boolean =
        mOrientationHelper.getDecoratedStart(child) >= mOrientationHelper.totalSpace
                || mOrientationHelper.getDecoratedEnd(child) <= 0

    private fun saveMedianChild() {
        val rvMedianOffset = mOrientationHelper.totalSpace / 2
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childStartOffset = mOrientationHelper.getDecoratedStart(child)
            val childEndOffset = mOrientationHelper.getDecoratedEnd(child)
            if (rvMedianOffset in childStartOffset..childEndOffset) {
                mLayoutState = LayoutState(getPosition(child))
                break
            }
        }
    }

    private fun layoutChildrenForScrolling(recycler: Recycler, state: State, available: Int, layoutDirection: LayoutDirection) {
        // scroll from end to start
        val edgeChild = getEdgeChild(layoutDirection) ?: return
        val currentOffset =
            if (layoutDirection == LayoutDirection.TOWARDS_END)
                mOrientationHelper.getDecoratedEnd(edgeChild)
            else
                mOrientationHelper.getDecoratedStart(edgeChild)
        var nextChildPosition = getPosition(edgeChild) + layoutDirection.v
        var consumed = 0
        while (consumed < available && nextChildPosition in 0 until state.itemCount) {
            val startOffset = currentOffset + consumed * layoutDirection.v
            val used = layoutChild(recycler, state, nextChildPosition, startOffset, layoutDirection)
            consumed += used
            nextChildPosition += layoutDirection.v
        }
//        if (delta > 0) {
//            val lastChild = getChildAt(childCount - 1) ?: return
//            var currentOffset = getDecoratedBottom(lastChild)
//            var nextChildPosition = getPosition(lastChild) + 1
//            var availableSpace = delta
//            while (availableSpace > 0 && nextChildPosition < state.itemCount) {
//                val used = layoutChild(recycler, state, nextChildPosition, currentOffset, LayoutDirection.TOWARDS_END)
//                currentOffset += used
//                availableSpace -= used
//                nextChildPosition++
//            }
//        }
//        if (delta < 0) {
//            val firstChild = getChildAt(0) ?: return
//            val currentFirstChildPosition = getPosition(firstChild)
//            var currentOffset = getDecoratedTop(firstChild)
//            var nextChildPosition = currentFirstChildPosition - 1
//            var availableSpace = delta
//            while (availableSpace < 0 && nextChildPosition > -1) {
//                val used = layoutChild(recycler, state, nextChildPosition, currentOffset, LayoutDirection.TOWARDS_START)
//                currentOffset -= used
//                availableSpace += used
//                nextChildPosition--
//            }
//        }
    }

    override fun onSaveInstanceState(): Parcelable {
        if (mLayoutState != null) {
            return mLayoutState!!
        }
        return LayoutState(if (childCount > 0) 0 else RecyclerView.NO_POSITION)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is LayoutState) {
            mLayoutState = state
            requestLayout()
        }
    }

    class LayoutState(val mMedianPosition: Int = RecyclerView.NO_POSITION) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readInt())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(mMedianPosition)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LayoutState> {
            override fun createFromParcel(parcel: Parcel): LayoutState {
                return LayoutState(parcel)
            }

            override fun newArray(size: Int): Array<LayoutState> {
                return Array(size) { LayoutState() }
            }
        }

    }
}