package com.j.mediaview.beans;

import android.net.Uri;

public class Video {
    private String path;
    private String displayName;
    private long size;
    private Uri uri;

    public Video(String path, String displayName, long size, Uri uri) {
        this.path = path;
        this.displayName = displayName;
        this.size = size;
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}