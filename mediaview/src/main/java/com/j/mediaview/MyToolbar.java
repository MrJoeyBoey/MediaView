package com.j.mediaview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;

import com.google.android.material.appbar.MaterialToolbar;

public class MyToolbar extends MaterialToolbar {

    private final Context c;

    private AppCompatTextView mTitleTextView;

    private CharSequence title;
    private ColorStateList colorStateList;
    private float titleTextSize;

    public MyToolbar(@NonNull Context context) {
        this(context, null);
    }
    public MyToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    @SuppressLint("RestrictedApi")
    public MyToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.c = context;

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        title = a.getText(R.styleable.Toolbar_title);
        if (a.hasValue(R.styleable.Toolbar_titleTextColor)) {
            colorStateList = a.getColorStateList(R.styleable.Toolbar_titleTextColor);
        }
        titleTextSize = 20;

        initTitleTextView();
    }

    public void initTitleTextView(){
        mTitleTextView = new AppCompatTextView(c);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);

        if (!TextUtils.isEmpty(title)) mTitleTextView.setText(title);
        if (colorStateList != null) mTitleTextView.setTextColor(colorStateList);
        mTitleTextView.setTextSize(titleTextSize);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTitleTextView.setLayoutParams(params);

        this.addView(mTitleTextView);
    }

    @Override
    public void setTitle(CharSequence title) { //复写本方法，防止外部调用导致显示出原本的title
        //this.title = title;
    }

    public void setMyTitle(CharSequence title) {
        this.title = title;
        mTitleTextView.setText(title);
    }

    public void setMyTitleTextColor(@NonNull ColorStateList colorStateList){
        this.colorStateList = colorStateList;
        mTitleTextView.setTextColor(colorStateList);
    }

    public void setTitleTextSize(float titleTextSize){
        this.titleTextSize = titleTextSize;
        mTitleTextView.setTextSize(titleTextSize);
    }

}
