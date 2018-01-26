/*
 * Thanks for Thien Nguyen
 */
package motocitizen.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class BounceScrollView : ScrollView {
    companion object {
        private const val MAX_Y_OVER_SCROLL_DISTANCE = 40
    }

    init {
        val metrics = context.resources.displayMetrics
        val density = metrics.density

        mMaxYOverScrollDistance = (density * MAX_Y_OVER_SCROLL_DISTANCE).toInt()
    }

    private var mMaxYOverScrollDistance: Int = 0
    private var isRequestedUpdate = false
    private var listener: () -> Unit = {}

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setOverScrollListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun overScrollBy(deltaX: Int,
                              deltaY: Int,
                              scrollX: Int,
                              scrollY: Int,
                              scrollRangeX: Int,
                              scrollRangeY: Int,
                              maxOverScrollX: Int,
                              maxOverScrollY: Int,
                              isTouchEvent: Boolean): Boolean {

        if (scrollY < -mMaxYOverScrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true
        }
        if (scrollY > -mMaxYOverScrollDistance * 0.1 && isRequestedUpdate) {
            isRequestedUpdate = false
            listener()
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverScrollDistance, isTouchEvent)
    }
}