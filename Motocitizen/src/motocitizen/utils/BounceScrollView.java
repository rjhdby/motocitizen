/*
 * Thanx for Thien Nguyen
 */
package motocitizen.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONObject;

import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.Startup;

public class BounceScrollView extends ScrollView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 40;

    private Context mContext;
    private int     mMaxYOverscrollDistance;
    private boolean isRequestedUpdate = false;

    public BounceScrollView(Context context) {
        super(context);
        mContext = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceScrollView();
    }

    private void initBounceScrollView() {
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float          density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
        //Log.d("OVERSCROLL", String.valueOf(scrollY));
        if (scrollY < -mMaxYOverscrollDistance * 0.9 && !isRequestedUpdate) {
            isRequestedUpdate = true;
        }
        if (scrollY > -mMaxYOverscrollDistance * 0.1 && isRequestedUpdate) {
            if (Startup.isOnline()) {
                isRequestedUpdate = false;
                if (Startup.mMenu != null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
                    Animation rotation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_refresh);
                    rotation.setRepeatCount(Animation.INFINITE);
                    iv.startAnimation(rotation);

                    MenuItem actionRefresh = Startup.mMenu.findItem(R.id.action_refresh);
                    actionRefresh.setActionView(iv);
                    actionRefresh.setVisible(true);
                }
                new AccidentsRequest(new AccidentsRequestCallback(), mContext);
            } else {
                Toast.makeText(mContext, R.string.inet_not_available, Toast.LENGTH_LONG).show();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (Startup.mMenu != null) {
                MenuItem item = Startup.mMenu.findItem(R.id.action_refresh);
                if (item.getActionView() != null) {
                    item.getActionView().clearAnimation();
                    item.setActionView(null);
                }
                item.setVisible(false);
            }
        }
    }
}