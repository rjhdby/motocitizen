/*
 * Thanx for Thien Nguyen
 */
package motocitizen.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class BounceScrollView : ScrollView {

    private var mMaxYOverScrollDistance: Int = 0
    private var isRequestedUpdate = false
    private var listener: OverScrollListenerInterface? = null

    constructor(context: Context) : super(context) {
        initBounceScrollView()
    }

    fun setOverScrollListener(listener: OverScrollListenerInterface) {
        this.listener = listener
    }

    private fun initBounceScrollView() {
        val metrics = context.resources.displayMetrics
        val density = metrics.density

        mMaxYOverScrollDistance = (density * MAX_Y_OVER_SCROLL_DISTANCE).toInt()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initBounceScrollView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initBounceScrollView()
    }

    override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {

        if (scrollY < -mMaxYOverScrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true
        }
        if (scrollY > -mMaxYOverScrollDistance * 0.1 && isRequestedUpdate) {
            isRequestedUpdate = false
            if (listener == null) return false
            listener!!.onOverScroll()
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverScrollDistance, isTouchEvent)
    }

    companion object {
        private val MAX_Y_OVER_SCROLL_DISTANCE = 40
    }
}