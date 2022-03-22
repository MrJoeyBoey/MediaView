package com.j.mediaview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.j.mediaview.R;
import com.j.simpledialog.MyView;

public class BitmapUtil {

    public static Bitmap getBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap addWaterMark(Context c, Bitmap src, String[] marks){
        try {
            MyView myView = new MyView(c, R.layout.view_water_mark);
            myView.setText(R.id.tv_water_mark, StringUtil.arrayToString(marks, "\n"));
            Bitmap waterMark = BitmapUtil.getBitmap(myView.getView());

            Bitmap newBitmap = src.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(waterMark, 0, src.getHeight() - waterMark.getHeight(), null);
            return newBitmap;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

}
