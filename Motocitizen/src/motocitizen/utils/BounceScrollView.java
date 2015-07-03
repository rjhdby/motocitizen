/*
 * Thanx for Thien Nguyen
 */
package motocitizen.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.general.AccidentsGeneral;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.Startup;

public class BounceScrollView extends ScrollView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 40;

    private final Context context;
    private       int     mMaxYOverscrollDistance;
    private boolean isRequestedUpdate = false;
    private RefreshAnimation refreshAnimation;

    public BounceScrollView(Context context) {
        super(context);
        this.context = context;
        initBounceScrollView();
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

    private void initBounceScrollView() {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float          density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (scrollY < -mMaxYOverscrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true;
        }
        if (scrollY > -mMaxYOverscrollDistance * 0.1 && isRequestedUpdate) {
            if (Startup.isOnline(context)) {
                isRequestedUpdate = false;
                if (Startup.mMenu != null) {
                    MenuItem refreshItem = Startup.mMenu.findItem(R.id.action_refresh);
                    refreshAnimation = new RefreshAnimation(refreshItem);
                    refreshAnimation.onRefreshBeginning();
                }
                new AccidentsRequest(context, new AccidentsRequestCallback());
            } else {
                Toast.makeText(context, R.string.inet_not_available, Toast.LENGTH_LONG).show();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                AccidentsGeneral.refreshPoints(context, result);
            }
            if (Startup.mMenu != null) {
                MenuItem item = Startup.mMenu.findItem(R.id.action_refresh);
                if (item.getActionView() != null) {
                    refreshAnimation.onRefreshComplete();
                }
            }
        }
    }
}