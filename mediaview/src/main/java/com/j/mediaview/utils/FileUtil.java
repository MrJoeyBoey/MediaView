package com.j.mediaview.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {

    public static String saveBitmap(Context context, Bitmap bitmap) {
        return saveBitmap(bitmap,90, context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

    public static String saveBitmap(Bitmap bitmap, int compress, File dir) {
        try {
            File file = new File(dir, String.format("%s.jpg", System.currentTimeMillis()));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compress, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveFile(Context context, Uri parentUri, ContentValues values, File file) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(parentUri, values);
        if (uri != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                OutputStream os = resolver.openOutputStream(uri);
                if (os != null) {
                    int len;
                    byte[] bytes = new byte[1024];
                    while ((len = fis.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }

                    fis.close();
                    os.flush();
                    os.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                resolver.delete(uri, null,null);
            }
        }
        return false;
    }

}
