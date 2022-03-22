package com.j.mediaview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.j.mediaview.beans.Media;
import com.j.mediaview.beans.MediaType;
import com.j.mediaview.beans.Source;
import com.j.mediaview.databinding.ActivityMediaDetailBinding;
import com.j.mediaview.databinding.DialogPictureShareBinding;
import com.j.mediaview.holder.BaseViewHolder;
import com.j.mediaview.listeners.DownloadCallback;
import com.j.mediaview.utils.Constants;
import com.j.mediaview.utils.FileUtil;
import com.j.mediaview.utils.OkHttpUtil;
import com.j.mediaview.utils.PlatformUtil;
import com.j.mediaview.utils.ShareUtil;
import com.j.mediaview.utils.StatusBarUtil;
import com.j.mediaview.utils.ToastUtil;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.util.ArrayList;

public class MediaDetailActivity extends AppCompatActivity {

    private ActivityMediaDetailBinding b;

    private BottomSheetDialog bottomDialog;
    private DialogPictureShareBinding pictureShareBinding;

    private ArrayList<Media> medias;
    private int position;

    private static final int SAVE = -1;
    private static final int WE_CHAT = 0;
    private static final int QQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMediaDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(b.getRoot());

        initData();
        initView();
        initEvent();
    }

    private void initData(){
        position = getIntent().getIntExtra(Constants.MEDIA_POSITION, 0);
        medias = getIntent().getParcelableArrayListExtra(Constants.MEDIAS);
    }

    private void initView(){
        StatusBarUtil.initToolbar(this, b.mtb);
        b.mtb.setMyTitle(String.format("%s/%s", position + 1, medias.size()));
        b.tvDescription.setText(medias.get(position).getDescription());

        MyMultipleRecyclerAdapter<Media> mediaDetailAdapter =
                new MyMultipleRecyclerAdapter<Media>(this, R.layout.adapter_picture_detail, R.layout.adapter_video_detail) {
                    @Override
                    public void bindView(BaseViewHolder holder, Media media, int position) {
                        switch (media.getMediaType()) {
                            case PICTURE:
                                PhotoView photoView = holder.getView(R.id.photo_view);
                                Glide.with(MediaDetailActivity.this)
                                        .load(media.getSource() == Source.LOCAL ? media.getUri() : media.getUrl())
                                        .into(photoView);
                                photoView.setOnLongClickListener(view -> {
                                    showShareDialog(media);
                                    return false;
                                });
                                break;
                            case VIDEO:
                                VideoView videoView = holder.getView(R.id.video_view);
                                LottieAnimationView lav = holder.getView(R.id.lav);
                                MediaController mediaController = new MediaController(MediaDetailActivity.this);
                                if (media.getSource() == Source.LOCAL) {
                                    videoView.setVideoURI(media.getUri());
                                }
                                if (media.getSource() == Source.NETWORK) {
                                    videoView.setVideoPath(media.getUrl());
                                }
                                videoView.setOnInfoListener((mediaPlayer, what, extra) -> {
                                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                        lav.playAnimation();
                                        lav.setVisibility(View.VISIBLE);
                                    } else {
                                        lav.pauseAnimation();
                                        lav.setVisibility(View.GONE);
                                    }
                                    return true;
                                });
                                videoView.setMediaController(mediaController);
                                videoView.start();
                                break;
                        }
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return getItem(position).getMediaType() == MediaType.PICTURE ? 0 : 1;
                    }
                };

        mediaDetailAdapter.update(medias);
        b.vp2.setAdapter(mediaDetailAdapter);
        b.vp2.setCurrentItem(position, false);
    }

    private void initEvent(){
        b.vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                b.mtb.setMyTitle(String.format("%s/%s", position + 1, medias.size()));
                b.tvDescription.setText(medias.get(position).getDescription());
            }
        });

        b.mtb.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_share) {
                showShareDialog(medias.get(b.vp2.getCurrentItem()));
            }
            return false;
        });
    }

    private void showShareDialog(Media media){
        if (bottomDialog == null) {
            bottomDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
            pictureShareBinding = DialogPictureShareBinding.inflate(LayoutInflater.from(this), null, false);
            bottomDialog.setContentView(R.layout.dialog_picture_share);
        }

        pictureShareBinding.tvCancel.setOnClickListener(view -> {
            bottomDialog.dismiss();
        });

        pictureShareBinding.tvWechat.setOnClickListener(view -> {
            bottomDialog.dismiss();
            if (media.getSource() == Source.LOCAL) {
                ShareUtil.shareToWeChat(MediaDetailActivity.this, media.getMediaType() == MediaType.PICTURE ? PlatformUtil.MEDIA_TYPE_IMAGE : PlatformUtil.MEDIA_TYPE_VIDEO, media.getUri());
            } else {
                download(media, WE_CHAT);
            }
        });

        pictureShareBinding.tvQq.setOnClickListener(view -> {
            bottomDialog.dismiss();
            if (media.getSource() == Source.LOCAL) {
                ShareUtil.shareToQQ(MediaDetailActivity.this, media.getMediaType() == MediaType.PICTURE ? PlatformUtil.MEDIA_TYPE_IMAGE : PlatformUtil.MEDIA_TYPE_VIDEO, media.getUri());
            } else {
                download(media, QQ);
            }
        });

        pictureShareBinding.tvCopy.setOnClickListener(view -> {
            bottomDialog.dismiss();
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null){
                cm.setPrimaryClip(ClipData.newPlainText(null, media.getSource() == Source.LOCAL ? String.valueOf(media.getUri()) : media.getUrl()));
                ToastUtil.showShort(this, "复制成功");
            } else {
                ToastUtil.showShort(this, "复制失败");
            }
        });

        pictureShareBinding.tvSave.setOnClickListener(view -> {
            bottomDialog.dismiss();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1
                );
            } else {
                if (media.getSource() == Source.LOCAL) {
                    ToastUtil.showShort(this, "本地已存在");
                } else {
                    download(media, SAVE);
                }
            }
        });

        bottomDialog.show();
    }

    private void download(Media media, int shareType){
        String path = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + System.currentTimeMillis() + (media.getMediaType() == MediaType.PICTURE ? ".jpg" : ".mp4");
        OkHttpUtil.download(path, media.getUrl(), new DownloadCallback() {
            @Override
            public void onSuccess(File file) {
                Uri uri = FileProvider7.getUriForFile(MediaDetailActivity.this, file);
                runOnUiThread(() -> {
                    if (shareType == SAVE) {
                        boolean success = FileUtil.saveFile(MediaDetailActivity.this,
                                media.getMediaType() == MediaType.PICTURE ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                new ContentValues(),
                                file
                        );
                        if (success) {
                            ToastUtil.showShort(MediaDetailActivity.this,"保存成功");
                        } else {
                            ToastUtil.showShort(MediaDetailActivity.this, "保存失败");
                        }
                    }
                    if (shareType == WE_CHAT) {
                        ShareUtil.shareToWeChat(MediaDetailActivity.this, media.getMediaType() == MediaType.PICTURE ? PlatformUtil.MEDIA_TYPE_IMAGE : PlatformUtil.MEDIA_TYPE_VIDEO, uri);
                    }
                    if (shareType == QQ) {
                        ShareUtil.shareToQQ(MediaDetailActivity.this, media.getMediaType() == MediaType.PICTURE ? PlatformUtil.MEDIA_TYPE_IMAGE : PlatformUtil.MEDIA_TYPE_VIDEO, uri);
                    }
                });
            }
            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> {
                    ToastUtil.showShort(MediaDetailActivity.this, "下载失败,请稍后重试");
                });
            }
            @Override
            public void onLoading(long total, long current, float percent) {
                //LogUtil.outPut(total, current, percent);
            }
        });
    }
}