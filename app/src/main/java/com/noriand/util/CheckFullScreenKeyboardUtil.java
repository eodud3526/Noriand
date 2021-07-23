package com.noriand.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class CheckFullScreenKeyboardUtil {
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    public CheckFullScreenKeyboardUtil(final Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int statusBarHeight = getStatusBarHeight(activity);
                possiblyResizeChildOfContent(statusBarHeight);
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent(int statusBarHeight) {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                frameLayoutParams.height = (usableHeightSansKeyboard - heightDifference) + statusBarHeight;
            } else {
//                frameLayoutParams.height = usableHeightSansKeyboard + statusBarHeight;
                frameLayoutParams.height = (usableHeightSansKeyboard - heightDifference) + statusBarHeight;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    private int getStatusBarHeight(Activity activity) {
        int height = 0;
        Resources resources = activity.getResources();
        int idStatusBarHeight = resources.getIdentifier( "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = resources.getDimensionPixelSize(idStatusBarHeight);
        } else {
            height = 0;
        }
        return height;
    }
}