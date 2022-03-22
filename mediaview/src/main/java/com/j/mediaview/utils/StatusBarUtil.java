package com.j.mediaview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

public class StatusBarUtil {

    public static void initToolbar(Activity a, ViewGroup viewGroup){
        ViewUtil.setViewHeight(viewGroup, StatusBarUtil.getStatusBarHeight(a) - 20, true);
        ViewUtil.setPaddingTop(viewGroup, viewGroup.getPaddingTop() + StatusBarUtil.getStatusBarHeight(a), true);
        if (viewGroup instanceof Toolbar){
            ((Toolbar) viewGroup).setNavigationOnClickListener(view -> a.finish());
        }
    }

    public static void initStatusBar(Activity a, ViewGroup viewGroup){
        setStatusBar(a,true);
        setStatusBarColor(a, Color.parseColor("#38ADFF"));
        setStatusTextColor(a,false);
        if (viewGroup instanceof Toolbar){
            ((Toolbar) viewGroup).setNavigationOnClickListener(view -> a.finish());
        }
    }

    public static void setStatusBar(Activity activity, boolean hasStatusBar) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasStatusBar){
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_VISIBLE ;
            decorView.setSystemUiVisibility(option);
        } else {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            setStatusBarColor(activity,Color.TRANSPARENT);
        }
    }

    public static void setStatusBarColor(Activity activity,int statusColor) {
        activity.getWindow().setStatusBarColor(statusColor);
    }

    public static void setStatusTextColor(Activity activity,boolean useDart) {
        View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useDart) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }

}
