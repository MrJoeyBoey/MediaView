package com.j.mediaview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j.mediaview.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class MyMultipleRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private List<T> mData;
    private final Context context;
    private final int[] resLayouts;

    protected MyMultipleRecyclerAdapter(Context context, int resLayout) {
        this.context = context;
        this.resLayouts = new int[]{resLayout};
        mData = new ArrayList<>();
    }

    protected MyMultipleRecyclerAdapter(Context context, int resLayout, List<T> mData) {
        this.context = context;
        this.resLayouts = new int[]{resLayout};
        this.mData = mData;
    }

    //需要重写getItemViewType方法自行给布局分类
    public MyMultipleRecyclerAdapter(Context context, int... resLayouts) {
        this.context = context;
        this.resLayouts = resLayouts;
        mData = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(resLayouts[viewType], parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        bindView(holder,mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public T getItem(int position){
        return this.mData == null ? null : this.mData.get(position);
    }

    public List<T> getmData() {
        return mData;
    }

    public void setmData(List<T> mData) {
        this.mData = mData;
    }

    public void update(List<T> mData){
        this.mData = mData;
        update();
    }

    public void add(T t){
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        this.mData.add(t);
        update();
    }

    public void remove(T t){
        if (this.mData == null) return;
        this.mData.remove(t);
        update();
    }

    public void update(){
        notifyDataSetChanged();
    }

    public abstract void bindView(BaseViewHolder holder, T t, int position);

}