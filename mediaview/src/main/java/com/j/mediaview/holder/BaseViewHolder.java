package com.j.mediaview.holder;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    private final SparseArray<View> mViews;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        mViews = new SparseArray<>();
    }

    public View getItemView(){
        return itemView;
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(@IdRes int res) {
        View v = mViews.get(res);
        if (v == null) {
            v = itemView.findViewById(res);
            mViews.put(res, v);
        }
        return (V) v;
    }

    public void setText(@IdRes int id,String text){
        View view = getView(id);
        if (view instanceof TextView){
            ((TextView)view).setText(text);
        }
    }

    public void setText(@IdRes int id, CharSequence chars){
        View view = getView(id);
        if (view instanceof TextView){
            ((TextView)view).setText(chars);
        }
    }

    public void append(@IdRes int id, CharSequence chars){
        View view = getView(id);
        if (view instanceof TextView){
            ((TextView) view).append(chars);
        }
    }

    public void setTextSize(@IdRes int id, int textSize){
        View view = getView(id);
        if (view instanceof TextView){
            ((TextView)view).setTextSize(textSize);
        }
    }

    public void setTextColor(@IdRes int id,int color){
        View view = getView(id);
        if (view instanceof TextView){
            ((TextView)view).setTextColor(color);
        }
    }

    public void setImageResource(@IdRes int id, int srcID){
        View view = getView(id);
        if (view instanceof ImageView){
            ((ImageView)view).setImageResource(srcID);
        }
    }

    public void setVisibility(int id,boolean visible){
        if (visible) getView(id).setVisibility(View.VISIBLE);
        else getView(id).setVisibility(View.GONE);
    }

    public void setSelect(int id,boolean select){
        getView(id).setSelected(select);
    }

    public void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener){
        getView(id).setOnClickListener(onClickListener);
    }

    public void setOnLongClickListener(@IdRes int id, View.OnLongClickListener onLongClickListener){
        getView(id).setOnLongClickListener(onLongClickListener);
    }

    public void setBackgroundColor(@IdRes int id, int color){
        getView(id).setBackgroundColor(color);
    }

    public void setBackgroundDrawable(@IdRes int id, Drawable drawable){
        getView(id).setBackground(drawable);
    }

    public void setBackgroundResource(@IdRes int id, int source){
        getView(id).setBackgroundResource(source);
    }

    public void setEnable(@IdRes int id, boolean enable){
        getView(id).setEnabled(enable);
    }


    public void setHint(@IdRes int id, String hint){
        View view = getView(id);
        if (view instanceof EditText) {
            ((EditText) view).setHint(hint);
        }
    }

    //自行扩展方法

}