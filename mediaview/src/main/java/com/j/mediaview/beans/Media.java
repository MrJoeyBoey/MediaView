package com.j.mediaview.beans;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {
    private MediaType mediaType;
    private Source source;
    private String url;
    private Uri uri;
    private String description;

    public Media(MediaType mediaType, Source source, String url, String description) {
        this.mediaType = mediaType;
        this.source = source;
        this.url = url;
        this.description = description;
    }

    public Media(MediaType mediaType, Source source, Uri uri, String description) {
        this.mediaType = mediaType;
        this.source = source;
        this.uri = uri;
        this.description = description;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected Media(Parcel in) {
        mediaType = (MediaType) in.readSerializable();
        source = (Source) in.readSerializable();
        url = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        description = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
                return new Media(in);
        }
        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(mediaType);
        parcel.writeSerializable(source);
        parcel.writeString(url);
        parcel.writeParcelable(uri, i);
        parcel.writeString(description);
    }
}