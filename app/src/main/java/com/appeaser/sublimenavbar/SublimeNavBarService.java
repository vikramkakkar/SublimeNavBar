/*
 * Copyright 2016 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appeaser.sublimenavbar;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Admin on 10/13/2016.
 */

public class SublimeNavBarService extends AccessibilityService {

    private static final String TAG = "SublimeNavBarService";

    private WindowManager mWindowManager;

    private View mNavBarView;
    private TextView tvAppName;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Log.i(TAG, "SublimeNavBarService connected");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service. `TYPE_WINDOW_STATE_CHANGED`
        // has been used only for demo purposes.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        // no delay before we are notified about an accessibility event
        info.notificationTimeout = 0;

        setServiceInfo(info);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        addNavView();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "onAccessibilityEvent");

        // Can't proceed
        if (tvAppName == null) {
            return;
        }

        CharSequence display = null;
        PackageManager pm = getPackageManager();

        // We'll retrieve and display the current application's label
        if (event != null && !TextUtils.isEmpty(event.getPackageName())) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(event.getPackageName().toString(), 0);

                if (appInfo != null) {
                    display = pm.getApplicationLabel(appInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // update label
        tvAppName.setText(display);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");
        Log.i(TAG, "Orientation is: "
                + (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait"
                : (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "landscape"
                : "unknown")));

        // Act on orientation change

        // #addNavView() can be modified to handle both orientations.
        // If we make the required changes, we will call the following
        // methods:

        // tryRemovingNavView();
        // addNavView();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void addNavView() {

        // On Marshmallow & above, we need to get user's permission
        // before we can use `SYSTEM_ALERT_WINDOW`. See `InitializationAct`
        // for more info.
        if (Utils.IS_AT_LEAST_MARSHMALLOW && !Settings.canDrawOverlays(this)) {
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // nav bar height since we're only designing for the portrait orientation
        int navBarSize = getResources().getDimensionPixelSize(R.dimen.nav_bar_size);

        // view that will be added/removed
        mNavBarView = LayoutInflater.from(this).inflate(R.layout.view_nav_bar, null);

        // will show current application's label
        tvAppName = (TextView) mNavBarView.findViewById(R.id.tv_app_name);

        // shows an image from http://lorempixel.com
        ImageView ivImage = (ImageView) mNavBarView.findViewById(R.id.iv_image);

        String imageLink = "http://lorempixel.com/"
                + displayMetrics.widthPixels + "/" + navBarSize + "/abstract";

        Picasso.with(this).load(imageLink).into(ivImage);

        // PORTRAIT orientation
        WindowManager.LayoutParams lpNavView = new WindowManager.LayoutParams();

        // match the screen's width
        lpNavView.width = WindowManager.LayoutParams.MATCH_PARENT;

        // height was looked up in the framework's source code
        lpNavView.height = navBarSize;

        // start from the left edge
        lpNavView.x = 0;

        // see the comment for WindowManager.LayoutParams#y to
        // understand why this is needed.
        lpNavView.y = -navBarSize;

        // we need this to draw over other apps
        lpNavView.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        // Lets us draw outside screen bounds
        lpNavView.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        // Since we are using Gravity.BOTTOM to position the view,
        // any value we specify to WindowManager.LayoutParams#y
        // will be measured from the bottom edge of the screen.
        // At y = 0, the view's bottom edge will sit just above
        // the navigation bar. A positive value such as y = 50 will
        // make our view 50 pixels above the top edge of the nav bar.
        // That's why we choose a negative value equal to the nav bar's height.
        lpNavView.gravity = Gravity.BOTTOM;

        // add the view
        mWindowManager.addView(mNavBarView, lpNavView);
    }

    /**
     * Try removing the view from the window.
     */
    private void tryRemovingNavView() {
        // if the window token is not null, the view is attached/added
        if (mNavBarView != null && mNavBarView.getWindowToken() != null) {
            mWindowManager.removeView(mNavBarView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "SublimeNavBarService destroyed");

        // clean up

        // remove  the view
        tryRemovingNavView();
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "SublimeNavBarService interrupted");

        // clean up

        // remove  the view
        tryRemovingNavView();
    }
}
