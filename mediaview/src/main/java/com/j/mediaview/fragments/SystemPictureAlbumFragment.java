package com.j.mediaview.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.j.mediaview.MyMapRecyclerAdapter;
import com.j.mediaview.MyMultipleRecyclerAdapter;
import com.j.mediaview.R;
import com.j.mediaview.beans.Check;
import com.j.mediaview.databinding.FragmentSystemPictureAlbumBinding;
import com.j.mediaview.holder.BaseViewHolder;
import com.j.mediaview.utils.CheckArray;
import com.j.mediaview.utils.DisplayUtil;
import com.j.mediaview.utils.ToastUtil;
import com.j.mediaview.utils.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SystemPictureAlbumFragment extends BaseBottomSheetFragment {

    private FragmentSystemPictureAlbumBinding b;

    private MyMapRecyclerAdapter<String, List<Uri>> pictureFolderAdapter;
    private MyMultipleRecyclerAdapter<Check<Uri>> pictureAdapter;

    private LinearLayoutManager l;
    private GridLayoutManager g;

    private OnChooseFinishListener onChooseFinishListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentSystemPictureAlbumBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initEvent();
        requestPictureFolder();
    }

    private void initView(){
        this.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        this.getBehavior().setSkipCollapsed(true);

        b.mtb.setTitleTextSize(17);

        l = new LinearLayoutManager(mContext);
        g = new GridLayoutManager(mContext, 3);

        pictureFolderAdapter = new MyMapRecyclerAdapter<String, List<Uri>>(mContext, R.layout.adapter_folder) {
            @Override
            public void bindView(BaseViewHolder holder, String dirName, List<Uri> uris, int position) {
                if (uris != null && uris.size() > 0) {
                    Glide.with(mContext)
                            .load(uris.get(0))
                            .error(R.mipmap.icon_no_picture)
                            .into((ImageView) holder.getView(R.id.iv_first_picture));
                }
                holder.setText(R.id.tv_folder_name, String.format("%s(%s)", dirName, uris == null ? 0 : uris.size()));

                holder.setOnClickListener(R.id.cl_picture_folder, view -> {
                    switchPictures(dirName, uris);
                });
            }
        };

        pictureAdapter = new MyMultipleRecyclerAdapter<Check<Uri>>(mContext) {
            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                BaseViewHolder holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_check_picture, parent, false));
                int size = DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dp2px(2) * 4;
                ViewUtil.setViewSize(holder.getView(R.id.iv_picture), size / 3, size / 3);
                return holder;
            }
            @Override
            public void bindView(BaseViewHolder holder, Check<Uri> picture, int position) {
                Glide.with(mContext)
                        .load(picture.getData())
                        .into((ImageView) holder.getView(R.id.iv_picture));
                holder.setBackgroundResource(R.id.iv_check,
                        picture.isChecked() ? R.mipmap.icon_picture_checked : R.mipmap.icon_picture_uncheck
                );
                holder.setOnClickListener(R.id.cl_picture, view -> {
                    picture.setChecked(!picture.isChecked());
                    this.update();
                });
            }
        };

        switchAlbums();
    }

    private void initEvent(){
        b.mtb.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_submit) {
                List<Uri> uris = CheckArray.getCheckItems(pictureAdapter.getmData());
                if (uris.size() < 1) {
                    ToastUtil.showShort(mContext, "请选择照片");
                } else {
                    if (onChooseFinishListener != null) onChooseFinishListener.onChooseFinish(uris);
                    this.dismiss();
                }
            }
            return false;
        });
    }

    private void switchAlbums(){
        b.mtb.setNavigationIcon(R.mipmap.icon_cross);
        b.mtb.setNavigationOnClickListener(view -> {
            this.dismiss();
        });
        b.mtb.setMyTitle("选择相册");
        b.mtb.getMenu().findItem(R.id.item_submit).setVisible(false);

        b.rvAlbum.setLayoutManager(l);
        b.rvAlbum.setAdapter(pictureFolderAdapter);
    }

    private void switchPictures(String albumName, List<Uri> uris){
        b.mtb.setNavigationIcon(R.mipmap.icon_back);
        b.mtb.setNavigationOnClickListener(view -> {
            switchAlbums();
        });
        b.mtb.setMyTitle(albumName);
        b.mtb.getMenu().findItem(R.id.item_submit).setVisible(true);

        b.rvAlbum.setLayoutManager(g);
        b.rvAlbum.setAdapter(pictureAdapter);

        pictureAdapter.update(CheckArray.asCheckArray(uris));
    }

    private void requestPictureFolder(){
        new Thread(() -> {
            LinkedHashMap<String, List<Uri>> picturesMap = new LinkedHashMap<>();
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor mCursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.MediaColumns._ID},
                    MediaStore.Images.Media.MIME_TYPE + " = ? or "+
                            MediaStore.Images.Media.MIME_TYPE + " = ? or "+
                            MediaStore.Images.Media.MIME_TYPE + " = ? ",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    String.format("%s desc", Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Images.Media.DATE_TAKEN : MediaStore.Images.Media.DATE_MODIFIED)
            );
            while (mCursor != null && mCursor.moveToNext()) {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)); // 获取图片的路径
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)); //获取文件名
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.MediaColumns._ID)); //获取文件ID
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)); //转换成Uri

                File dir = new File(path).getParentFile();
                if (dir != null) {
                    String dirPath = dir.getAbsolutePath();
                    String dirName = dir.getName();
                    if (picturesMap.containsKey(dirName)) {
                        List<Uri> uris = picturesMap.get(dirName);
                        if (uris != null) uris.add(uri);
                    } else {
                        List<Uri> uris = new ArrayList<>();
                        uris.add(uri);
                        picturesMap.put(dirName, uris);
                    }
                }
            }
            if (mCursor != null) mCursor.close();

            new Handler(Looper.getMainLooper()).post(() -> {
                pictureFolderAdapter.update(picturesMap);
            });
        }).start();
    }

    public void setOnChooseFinishListener(OnChooseFinishListener onChooseFinishListener) {
        this.onChooseFinishListener = onChooseFinishListener;
    }

    public interface OnChooseFinishListener {
        void onChooseFinish(List<Uri> uris);
    }

}
