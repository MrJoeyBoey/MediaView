package com.j.mediaview.beans;

public class Check<T> {

    private T data;
    private boolean isChecked;

    public Check(T data) {
        this.data = data;
    }

    public Check(T data, boolean isChecked) {
        this.data = data;
        this.isChecked = isChecked;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
