package motocitizen.utils;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import motocitizen.main.R;

public class RefreshAnimation {
    private final MenuItem  mRefreshItem;
    private final Animation rotate;
    private boolean mIsRefreshInProgress = false;

    public RefreshAnimation(MenuItem refreshItem) {
        mRefreshItem = refreshItem;
        rotate = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setFillAfter(true);
        rotate.setFillBefore(true);
        rotate.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!mIsRefreshInProgress)
                            stopAnimation();
                        rotate.setFillAfter(true);
                        rotate.setFillBefore(true);
                    }
                });
    }

    public void onRefreshBeginning() {
        if (mIsRefreshInProgress)
            return;
        mIsRefreshInProgress = true;

        stopAnimation();

        MenuItemCompat.setActionView(mRefreshItem, R.layout.iv_refresh);
        View actionView = MenuItemCompat.getActionView(mRefreshItem);
        if (actionView != null)
            actionView.startAnimation(rotate);
    }

    public void onRefreshComplete() {
        mIsRefreshInProgress = false;
    }

    private void stopAnimation() {

        View actionView = MenuItemCompat.getActionView(mRefreshItem);
        if (actionView == null)
            return;
        actionView.clearAnimation();
        MenuItemCompat.setActionView(mRefreshItem, null);
    }
}
