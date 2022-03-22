package com.j.mediaview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;
import java.util.List;

public class PlatformUtil {
    public static final String PN_GAODE_MAP = "com.autonavi.minimap";// 高德地图包名
    public static final String PN_BAIDU_MAP = "com.baidu.BaiduMap"; // 百度地图包名
    public static final String PN_TENCENT_MAP = "com.tencent.map"; // 腾讯地图包名

    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";

    public static final String CLASS_QQ_SHARE = "com.tencent.mobileqq.activity.JumpActivity";
    public static final String CLASS_WX_SHARE = "com.tencent.mm.ui.tools.ShareImgUI";

    public static final String MEDIA_TYPE_IMAGE = "image/jpg";
    public static final String MEDIA_TYPE_VIDEO = "video/mp4";

    public static final String FILE_TYPE_IMAGE = "image/*";
    public static final String FILE_TYPE_VIDEO = "video/*";
    public static final String FILE_TYPE_TEXT = "text/plain";
    public static final String FILE_TYPE_AUDIO = "audio/*";
    public static final String FILE_TYPE_FILE = "*/*";

    public static boolean isInstallApp(Context context, String app_package){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pInfo.size(); i++) {
            if (app_package.equals(pInfo.get(i).packageName)) return true;
        }
        return false;
    }

    public static String getAppVersionName(Context context){
        String appVersionName = "0.0.0";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionName =  packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }

    public static int getAppVersionCode(Context context) {
        int appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionCode =  packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionCode;
    }

}