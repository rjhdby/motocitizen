/*
 * Thanx for Thien Nguyen
 */
package motocitizen.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.content.Content;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.Startup;

public class BounceScrollView extends ScrollView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 40;

    private final Context context;
    private       int     mMaxYOverscrollDistance;
    private boolean isRequestedUpdate = false;

    public BounceScrollView(Context context) {
        super(context);
        this.context = context;
        initBounceScrollView();
    }

    private void initBounceScrollView() {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float          density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
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
        if (Startup.inTransaction) return true;
        if (scrollY < -mMaxYOverscrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true;
        }
        if (scrollY > -mMaxYOverscrollDistance * 0.1 && isRequestedUpdate) {
            if (Startup.isOnline()) {
                isRequestedUpdate = false;
                Startup.startRefreshAnimation();
                Content.update(new AccidentsRequestCallback());
                new AccidentsRequest(new AccidentsRequestCallback());
            } else {
                message(context.getString(R.string.inet_not_available));
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    private void message(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    message(result.getString("error"));
                } catch (JSONException e) {
                    message("Неизвестная ошибка");
                    e.printStackTrace();
                }
            } else {
                Content.parseJSON(result);
                Content.redraw();
            }
            Startup.stopRefreshAnimation();
        }
    }
}