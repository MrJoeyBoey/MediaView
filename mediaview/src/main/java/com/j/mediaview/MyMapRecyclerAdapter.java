package com.j.mediaview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j.mediaview.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class MyMapRecyclerAdapter<K, V> extends RecyclerView.Adapter<BaseViewHolder> {

    private final Context c;
    private final int layoutRes;

    private LinkedHashMap<K, V> map;
    private List<K> keys;

    public MyMapRecyclerAdapter(Context c, int layoutRes) {
        this.c = c;
        this.layoutRes = layoutRes;
        map = new LinkedHashMap<>();
        keys = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(c).inflate(layoutRes, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        bindView(holder, keys.get(position), map.get(keys.get(position)), position);
    }

    public abstract void bindView(BaseViewHolder holder, K k, V v, int position);

    @Override
    public int getItemCount() {
        return keys == null ? 0 : keys.size();
    }

    public void update(LinkedHashMap<K, V> map){
        this.map = map;
        this.keys = new ArrayList<>(map.keySet());
        this.update();
    }

    public void update(){
        this.notifyDataSetChanged();
    }

    public void add(K k, V v){
        this.map.put(k, v);
        this.keys = new ArrayList<>(map.keySet());
        this.update();
    }

    public void remove(K k){
        this.map.remove(k);
        this.keys = new ArrayList<>(map.keySet());
        this.update();
    }

    public LinkedHashMap<K, V> getMap() {
        return map;
    }
}
