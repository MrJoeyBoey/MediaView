package com.j.mediaview.listeners;

import java.io.File;

public interface DownloadCallback {
    void onSuccess(File file);
    void onFailed(Exception e);
    void onLoading(long total, long current, float percent);
}
