package com.j.mediaview.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showShort(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

}
