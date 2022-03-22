package com.j.mediaview.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import com.j.mediaview.beans.MediaType;

public class CameraUtil {

    private static final String TAG = "CameraUtil";

    public static final int REQUEST_PERMISSION_CODE = 1024;
    private static final int REQUEST_TAKE_PHOTOS_CODE = 2048;
    public static final int REQUEST_OPEN_SYSTEM_ALBUM_CODE = 4096;

    private static Uri PICTURE_URI;

    public static void openCamera(Activity a, int requestCode, MediaType mediaType){
        if(ActivityCompat.checkSelfPermission(a, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(a, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(a, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (mediaType == MediaType.PICTURE) {
                PICTURE_URI = insertPictureUri(a);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, PICTURE_URI);
                a.startActivityForResult(intent, requestCode);
            }
            if (mediaType == MediaType.VIDEO) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                a.startActivityForResult(intent, requestCode);
            }
        } else {
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE
            );
        }
    }

    public static Uri insertPictureUri(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    public static Uri getPictureUri() {
        return PICTURE_URI;
    }

}
