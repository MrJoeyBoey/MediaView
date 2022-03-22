package com.j.mediaview.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

    public static void setViewSize(View view, int width, int height){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.requestLayout();
    }

    public static void setPaddingTop(View view, int top){
        setPaddingTop(view, top,false);
    }
    public static void setPaddingTop(View view, int top, boolean keepOtherPadding){
        if (keepOtherPadding) {
            view.setPadding(view.getPaddingStart(), top, view.getPaddingEnd(), view.getPaddingBottom());
        } else {
            view.setPadding(0, top, 0, 0);
        }
    }

    public static void setViewHeight(View view, int height){
        setViewHeight(view, height, false);
    }

    public static void setViewHeight(View view, int height, boolean accumulation){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = accumulation ? params.height + height : height;
        view.requestLayout();
    }

}
