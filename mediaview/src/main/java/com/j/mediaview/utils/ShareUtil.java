package com.j.mediaview.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShareUtil {

    public static void shareToWeChat(Context context, String fileType, Uri uri){
        shareToWeChat(context, fileType, Collections.singletonList(uri));
    }
    public static void shareToWeChat(Context context, String fileType, List<Uri> uris){
        if(!PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)){
            ToastUtil.showShort(context, "您需要安装微信APP");
            return;
        }

        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PlatformUtil.PACKAGE_WECHAT, PlatformUtil.CLASS_WX_SHARE);
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (uris == null || uris.size() < 1) {
            return;
        }
        if (uris.size() == 1) {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType(fileType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        } else if (uris != null && uris.size() > 1) {
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType(fileType);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<>(uris));
        }
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

    public static void shareToQQ(Context context, String fileType, Uri uri) {
        shareToQQ(context, fileType, Collections.singletonList(uri));
    }
    public static void shareToQQ(Context context, String fileType, List<Uri> uris) {
        if (!PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_MOBILE_QQ)) {
            ToastUtil.showShort(context, "您需要安装QQ APP");
            return;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(PlatformUtil.PACKAGE_MOBILE_QQ, PlatformUtil.CLASS_QQ_SHARE);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (uris == null || uris.size() < 1) {
            return;
        }
        if (uris.size() == 1) {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType(fileType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        } else if (uris != null && uris.size() > 1) {
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType(fileType);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<>(uris));
        }
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

}
