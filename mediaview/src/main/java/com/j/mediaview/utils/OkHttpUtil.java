package com.j.mediaview.utils;

import androidx.annotation.NonNull;
import com.j.mediaview.listeners.DownloadCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpUtil {

    public static void get(String url, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void download(String target, String url, DownloadCallback downloadCallback){
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    InputStream inputStream = body.byteStream();
                    long totalLength = body.contentLength(); //文件总大小

                    File file = new File(target);
                    FileOutputStream outputStream = new FileOutputStream(file);

                    int len;
                    int finishLen = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);

                        finishLen += len;

                        downloadCallback.onLoading(totalLength, finishLen, 1f * totalLength / finishLen);
                    }
                    downloadCallback.onSuccess(file);

                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                } else {
                    downloadCallback.onFailed(new NullPointerException("body 为空"));
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                downloadCallback.onFailed(e);
            }
        });
    }

}
