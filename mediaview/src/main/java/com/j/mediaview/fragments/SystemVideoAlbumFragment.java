package com.j.mediaview.fragments;

import android.content.ContentResolver;
import android.content.Intent;
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
import com.j.mediaview.MediaView;
import com.j.mediaview.MyMapRecyclerAdapter;
import com.j.mediaview.MyMultipleRecyclerAdapter;
import com.j.mediaview.R;
import com.j.mediaview.beans.Check;
import com.j.mediaview.beans.Media;
import com.j.mediaview.beans.MediaType;
import com.j.mediaview.beans.Source;
import com.j.mediaview.beans.Video;
import com.j.mediaview.databinding.FragmentSystemVideoAlbumBinding;
import com.j.mediaview.holder.BaseViewHolder;
import com.j.mediaview.utils.CheckArray;
import com.j.mediaview.utils.DisplayUtil;
import com.j.mediaview.utils.ToastUtil;
import com.j.mediaview.utils.ViewUtil;
import com.j.simpledialog.SimpleDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SystemVideoAlbumFragment extends BaseBottomSheetFragment {

    private FragmentSystemVideoAlbumBinding b;

    private MyMapRecyclerAdapter<String, List<Video>> videoFolderAdapter;
    private MyMultipleRecyclerAdapter<Check<Video>> videoAdapter;

    private LinearLayoutManager l;
    private GridLayoutManager g;

    private OnChooseFinishListener onChooseFinishListener;

    private long limitVideoSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentSystemVideoAlbumBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        initEvent();
        requestVideoFolder();
    }

    private void initData(){
        limitVideoSize = 100 * 1024 * 1024; // 默认100MB
    }

    private void initView(){
        this.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        this.getBehavior().setSkipCollapsed(true);

        b.mtb.setTitleTextSize(17);

        l = new LinearLayoutManager(mContext);
        g = new GridLayoutManager(mContext, 3);

        videoFolderAdapter = new MyMapRecyclerAdapter<String, List<Video>>(mContext, R.layout.adapter_folder) {
            @Override
            public void bindView(BaseViewHolder holder, String dirName, List<Video> videos, int position) {
                if (videos != null && videos.size() > 0) {
                    Glide.with(mContext)
                            .load(videos.get(0).getUri())
                            .error(R.mipmap.icon_no_picture)
                            .into((ImageView) holder.getView(R.id.iv_first_picture));
                }
                holder.setText(R.id.tv_folder_name, String.format("%s(%s)", dirName, videos == null ? 0 : videos.size()));

                holder.setOnClickListener(R.id.cl_picture_folder, view -> {
                    switchPictures(dirName, videos);
                });
            }
        };

        videoAdapter = new MyMultipleRecyclerAdapter<Check<Video>>(mContext) {
            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                BaseViewHolder holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_check_video, parent, false));
                int size = DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dp2px(2) * 4;
                ViewUtil.setViewSize(holder.getView(R.id.iv_video), size / 3, size / 3);
                return holder;
            }
            @Override
            public void bindView(BaseViewHolder holder, Check<Video> video, int position) {
                Glide.with(mContext)
                        .load(video.getData().getUri())
                        .into((ImageView) holder.getView(R.id.iv_video));
                holder.setBackgroundResource(R.id.iv_check,
                        video.isChecked() ? R.mipmap.bg_cover_check: R.mipmap.bg_cover_uncheck
                );
                holder.setOnClickListener(R.id.cl_video, view -> {
                    if (video.getData().getSize() < limitVideoSize) {
                        video.setChecked(!video.isChecked());
                        this.update();
                    } else {
                        new SimpleDialog.Builder(mContext)
                                .setTitle("提示")
                                .setContent(String.format("请选择小于%sMB的视频文件", limitVideoSize / 1024 / 1024))
                                .setNegativeButtonVisible(false)
                                .show();
                    }
                });
                holder.setOnLongClickListener(R.id.cl_video, view -> {
                    //预览视频
                    Media media = new Media(MediaType.VIDEO, Source.LOCAL, video.getData().getUri(), "");
                    /*Intent intent = new Intent(mContext, MediaDetailActivity.class);
                    intent.putExtra(Constants.MEDIA_POSITION, 0);
                    intent.putParcelableArrayListExtra(Constants.MEDIAS, new ArrayList<>(Collections.singleton(media)));
                    startActivity(intent);*/
                    return false;
                });
            }
        };

        switchAlbums();
    }

    private void initEvent(){
        b.mtb.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_submit) {
                List<Video> videos = CheckArray.getCheckItems(videoAdapter.getmData());
                if (videos.size() < 1) {
                    ToastUtil.showShort(mContext, "请选择视频");
                } else {
                    List<Uri> uris = new ArrayList<>();
                    for (Video video : videos) {
                        uris.add(video.getUri());
                    }
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
        b.mtb.setMyTitle("选择视频册");
        b.mtb.getMenu().findItem(R.id.item_submit).setVisible(false);

        b.rvAlbum.setLayoutManager(l);
        b.rvAlbum.setAdapter(videoFolderAdapter);
    }

    private void switchPictures(String albumName, List<Video> videos){
        b.mtb.setNavigationIcon(R.mipmap.icon_back);
        b.mtb.setNavigationOnClickListener(view -> {
            switchAlbums();
        });
        b.mtb.setMyTitle(albumName);
        b.mtb.getMenu().findItem(R.id.item_submit).setVisible(true);

        b.rvAlbum.setLayoutManager(g);
        b.rvAlbum.setAdapter(videoAdapter);

        videoAdapter.update(CheckArray.asCheckArray(videos));
    }

    private void requestVideoFolder(){
        new Thread(() -> {
            LinkedHashMap<String, List<Video>> picturesMap = new LinkedHashMap<>();
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor mCursor = contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.MediaColumns._ID},
                    MediaStore.Video.Media.MIME_TYPE + " = ? or "+
                            MediaStore.Video.Media.MIME_TYPE + " = ? ",
                    new String[]{"video/mp4", "video/avi"},
                    String.format("%s desc", Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Video.Media.DATE_TAKEN : MediaStore.Video.Media.DATE_MODIFIED)
            );
            while (mCursor != null && mCursor.moveToNext()) {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA)); // 获取图片的路径
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)); //获取文件名
                long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.MediaColumns._ID)); //获取文件ID
                Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)); //转换成Uri

                File dir = new File(path).getParentFile();
                if (dir != null) {
                    String dirPath = dir.getAbsolutePath();
                    String dirName = dir.getName();
                    if (picturesMap.containsKey(dirName)) {
                        List<Video> videos = picturesMap.get(dirName);
                        if (videos != null) {
                            Video video = new Video(path, displayName, size, uri);
                            videos.add(video);
                        }
                    } else {
                        List<Video> videos = new ArrayList<>();
                        Video video = new Video(path, displayName, size, uri);
                        videos.add(video);
                        picturesMap.put(dirName, videos);
                    }
                }
            }
            if (mCursor != null) mCursor.close();

            new Handler(Looper.getMainLooper()).post(() -> {
                videoFolderAdapter.update(picturesMap);
            });
        }).start();
    }

    public void setLimitVideoSize(long limitVideoSize) {
        this.limitVideoSize = limitVideoSize;
    }

    public void setOnChooseFinishListener(OnChooseFinishListener onChooseFinishListener) {
        this.onChooseFinishListener = onChooseFinishListener;
    }

    public interface OnChooseFinishListener {
        void onChooseFinish(List<Uri> uris);
    }

}
