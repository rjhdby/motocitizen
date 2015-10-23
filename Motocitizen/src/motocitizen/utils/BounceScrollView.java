/*
 * Thanx for Thien Nguyen
 */
package motocitizen.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

public class BounceScrollView extends ScrollView {
    private static final int MAX_Y_OVER_SCROLL_DISTANCE = 40;

    private final Context context;
    private       int     mMaxYOverScrollDistance;
    private boolean isRequestedUpdate = false;
    private OverScrollListenerInterface listener;

    public BounceScrollView(Context context) {
        super(context);
        this.context = context;
        initBounceScrollView();
    }

    public void setOverScrollListener(OverScrollListenerInterface listener) {
        this.listener = listener;
    }

    private void initBounceScrollView() {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float          density = metrics.density;

        mMaxYOverScrollDistance = (int) (density * MAX_Y_OVER_SCROLL_DISTANCE);
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initBounceScrollView();
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        if (scrollY < -mMaxYOverScrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true;
        }
        if (scrollY > -mMaxYOverScrollDistance * 0.1 && isRequestedUpdate) {
            isRequestedUpdate = false;
            if (listener == null) return false;
            listener.onOverScroll();
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverScrollDistance, isTouchEvent);
    }
}