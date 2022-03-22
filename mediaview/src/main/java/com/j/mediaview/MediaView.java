package com.j.mediaview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.j.mediaview.beans.Media;
import com.j.mediaview.beans.MediaType;
import com.j.mediaview.beans.Source;
import com.j.mediaview.databinding.DialogMediaChooseBinding;
import com.j.mediaview.fragments.SystemPictureAlbumFragment;
import com.j.mediaview.fragments.SystemVideoAlbumFragment;
import com.j.mediaview.holder.BaseViewHolder;
import com.j.mediaview.utils.BitmapUtil;
import com.j.mediaview.utils.CameraUtil;
import com.j.mediaview.utils.Constants;
import com.j.mediaview.utils.DisplayUtil;
import com.j.mediaview.utils.FileUtil;
import com.j.mediaview.utils.ToastUtil;
import com.j.mediaview.utils.ViewUtil;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MediaView extends RecyclerView {

    private final Context c;

    private MediaAdapter mediaAdapter;
    private GridLayoutManager g;

    private SystemPictureAlbumFragment systemPictureAlbumFragment;
    private SystemVideoAlbumFragment systemVideoAlbumFragment;

    private int spanCount;
    private String description;

    private BottomSheetDialog mediaChooseDialog;
    private DialogMediaChooseBinding mediaChooseBinding;

    private int type;
    public static final int DEFAULT = 0;
    public static final int ONLY_VIEW = 1;

    private MediaMenu[] mediaMenus;

    public static final int AUTO = 0;
    public static final int NONE = 1;
    public static final int CUSTOM = 2;
    private int watermarkMode;
    private String[] watermarks;

    private int requestTakePictureCode = 1024;
    private int requestTakeVideoCode = 2048;

    private OnMediaChangeListener onMediaChangeListener;
    private OnViewMediaListener onViewMediaListener;

    private final MediaView thisView;

    public MediaView(@NonNull Context context) {
        this(context, null);
    }
    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.c = context;
        thisView = this;

        initView();
        initEvent();
    }

    private void initView(){
        this.spanCount = 4;
        this.type = DEFAULT;
        this.mediaMenus = new MediaMenu[]{MediaMenu.PICTURE_TAKE, MediaMenu.PICTURE_CHOOSE, MediaMenu.VIDEO_TAKE, MediaMenu.VIDEO_CHOOSE};
        this.watermarkMode = AUTO;

        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.g = new GridLayoutManager(c, spanCount);
        this.setLayoutManager(g);
        this.mediaAdapter = new MediaAdapter();
        this.setAdapter(mediaAdapter);
    }

    private void initEvent(){
        mediaAdapter.setOnMediaClickListener(position -> {
            if (type == DEFAULT) {
                if (mediaMenus.length == 1) {
                    if (position == -1) {
                        //直接打开
                        switch (mediaMenus[0]) {
                            case PICTURE_TAKE:
                                CameraUtil.openCamera((Activity) c, requestTakePictureCode, MediaType.PICTURE);
                                break;
                            case PICTURE_CHOOSE:
                                showSystemPictureAlbumFragment();
                                break;
                            case VIDEO_TAKE:
                                CameraUtil.openCamera((Activity) c, requestTakeVideoCode, MediaType.VIDEO);
                                break;
                            case VIDEO_CHOOSE:
                                showSystemVideoAlbumFragment();
                                break;
                        }
                    } else {
                        if (onViewMediaListener != null) {
                            onViewMediaListener.onViewMedia(position);
                        } else {
                            startMediaDetailActivity(position);
                        }
                    }
                } else {
                    if (position == -1) {
                        showMediaChooseDialog();
                    } else {
                        if (onViewMediaListener != null) {
                            onViewMediaListener.onViewMedia(position);
                        } else {
                            startMediaDetailActivity(position);
                        }
                    }
                }
            }

            if (type == ONLY_VIEW) {
                if (onViewMediaListener != null) {
                    onViewMediaListener.onViewMedia(position);
                } else {
                    startMediaDetailActivity(position);
                }
            }
        });
    }

    private void showMediaChooseDialog(){
        if (mediaChooseDialog == null) {
            mediaChooseDialog = new BottomSheetDialog(c, R.style.BottomSheetDialog);
            mediaChooseBinding = DialogMediaChooseBinding.inflate(LayoutInflater.from(c), null, false);
            mediaChooseDialog.setContentView(mediaChooseBinding.getRoot());
        }

        List<MediaMenu> mediaMenuList = Arrays.asList(mediaMenus);
        mediaChooseBinding.tvPictureTake.setVisibility(mediaMenuList.contains(MediaMenu.PICTURE_TAKE) ? VISIBLE : INVISIBLE);
        mediaChooseBinding.tvPictureChoose.setVisibility(mediaMenuList.contains(MediaMenu.PICTURE_CHOOSE) ? VISIBLE : INVISIBLE);
        mediaChooseBinding.tvVideoTake.setVisibility(mediaMenuList.contains(MediaMenu.VIDEO_TAKE) ? VISIBLE : INVISIBLE);
        mediaChooseBinding.tvVideoChoose.setVisibility(mediaMenuList.contains(MediaMenu.VIDEO_CHOOSE) ? VISIBLE : INVISIBLE);

        mediaChooseBinding.tvPictureTake.setOnClickListener(view -> {
            CameraUtil.openCamera((Activity) c, requestTakePictureCode, MediaType.PICTURE);
            mediaChooseDialog.dismiss();
        });

        mediaChooseBinding.tvPictureChoose.setOnClickListener(view -> {
            showSystemPictureAlbumFragment();
            mediaChooseDialog.dismiss();
        });

        mediaChooseBinding.tvVideoTake.setOnClickListener(view -> {
            CameraUtil.openCamera((Activity) c, requestTakeVideoCode, MediaType.VIDEO);
            mediaChooseDialog.dismiss();
        });

        mediaChooseBinding.tvVideoChoose.setOnClickListener(view -> {
            showSystemVideoAlbumFragment();
            mediaChooseDialog.dismiss();
        });

        mediaChooseBinding.tvCancel.setOnClickListener(view -> {
            mediaChooseDialog.dismiss();
        });

        mediaChooseDialog.show();
    }

    private void showSystemPictureAlbumFragment(){
        if (systemPictureAlbumFragment == null) {
            systemPictureAlbumFragment = new SystemPictureAlbumFragment();
            systemPictureAlbumFragment.setOnChooseFinishListener(uris -> {
                List<Media> medias = new ArrayList<>();
                for (Uri uri : uris){
                    Media media = new Media(MediaType.PICTURE, Source.LOCAL, uri, description);
                    medias.add(media);
                }
                mediaAdapter.addAll(medias);
            });
        }
        if (c instanceof FragmentActivity) {
            systemPictureAlbumFragment.show(((FragmentActivity) c).getSupportFragmentManager(), SystemPictureAlbumFragment.class.getName());
        } else {
            ToastUtil.showShort(c, "打开相册失败");
        }
    }

    private void showSystemVideoAlbumFragment(){
        if (systemVideoAlbumFragment == null) {
            systemVideoAlbumFragment = new SystemVideoAlbumFragment();
            systemVideoAlbumFragment.setOnChooseFinishListener(uris -> {
                List<Media> medias = new ArrayList<>();
                for (Uri uri : uris){
                    Media media = new Media(MediaType.VIDEO, Source.LOCAL, uri, description);
                    medias.add(media);
                }
                mediaAdapter.addAll(medias);
            });
        }
        if (c instanceof FragmentActivity) {
            systemVideoAlbumFragment.show(((FragmentActivity) c).getSupportFragmentManager(), SystemVideoAlbumFragment.class.getName());
        } else {
            ToastUtil.showShort(c, "打开视频册失败");
        }
    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        g.setSpanCount(this.spanCount);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MediaMenu[] getMediaMenus() {
        return mediaMenus;
    }

    public void setMediaMenus(MediaMenu... mediaMenus) {
        this.mediaMenus = mediaMenus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getWatermarks() {
        return watermarks;
    }

    public void setWatermarks(String[] watermarks) {
        this.watermarks = watermarks;
    }

    public int getWatermarkMode() {
        return watermarkMode;
    }

    public void setWatermarkMode(int watermarkMode) {
        this.watermarkMode = watermarkMode;
    }

    public void setMedias(List<Media> medias){
        setMedias("", medias);
    }

    public void setMedias(String description, List<Media> medias){
        this.description = description;
        mediaAdapter.update(medias);
    }

    public List<Media> getMedias() {
        return mediaAdapter.getMedias() == null ? new ArrayList<>() : mediaAdapter.getMedias();
    }

    public int getRequestTakePictureCode() {
        return requestTakePictureCode;
    }

    public void setRequestTakePictureCode(int requestTakePictureCode) {
        this.requestTakePictureCode = requestTakePictureCode;
    }

    public int getRequestTakeVideoCode() {
        return requestTakeVideoCode;
    }

    public void setRequestTakeVideoCode(int requestTakeVideoCode) {
        this.requestTakeVideoCode = requestTakeVideoCode;
    }

    public void setOnMediaChangeListener(OnMediaChangeListener onMediaChangeListener) {
        this.onMediaChangeListener = onMediaChangeListener;
    }

    public void setOnViewMediaListener(OnViewMediaListener onViewMediaListener) {
        this.onViewMediaListener = onViewMediaListener;
    }

    public void setupWithMediaView(MediaView... mediaViews){
        List<MediaView> mvs = new ArrayList<>(Arrays.asList(mediaViews));
        mvs.add(0, thisView);

        List<Media> medias = new ArrayList<>();
        for (MediaView mv : mvs) {
            medias.addAll(mv.getMedias());
        }

        for (MediaView mv : mvs) {
            int allFrontMediaSize = 0;
            for (int j = 0; j < mvs.indexOf(mv); j++ ){
                allFrontMediaSize += mvs.get(j).getMedias().size();
            }
            final int finalPosition = allFrontMediaSize;
            mv.setOnViewMediaListener(position -> {
                startMediaDetailActivity(position + finalPosition, medias);
            });
        }
    }

    private void startMediaDetailActivity(int position){
        startMediaDetailActivity(position, getMedias());
    }
    private void startMediaDetailActivity(int position, List<Media> medias){
        Intent intent = new Intent(c, MediaDetailActivity.class);
        intent.putExtra(Constants.MEDIA_POSITION, position);
        intent.putParcelableArrayListExtra(Constants.MEDIAS, new ArrayList<>(medias));
        c.startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == requestTakePictureCode && resultCode == Activity.RESULT_OK) {
            Uri uri = CameraUtil.getPictureUri();
            if (uri == null) {
                ToastUtil.showShort(c, "发生错误,请重新拍摄");
            } else {
                try {
                    Media media;
                    if (watermarkMode == NONE) {
                        media = new Media(MediaType.PICTURE, Source.LOCAL, uri, description);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri));
                        Bitmap newBitmap = BitmapUtil.addWaterMark(c, bitmap, watermarkMode == AUTO ? autoWatermarks() : watermarks);
                        File file = new File(FileUtil.saveBitmap(c, newBitmap));
                        Uri newUri = FileProvider7.getUriForFile(c, file);
                        media = new Media(MediaType.PICTURE, Source.LOCAL, newUri, description);
                    }
                    mediaAdapter.add(media);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        if (requestCode == requestTakeVideoCode && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            Media media = new Media(MediaType.VIDEO, Source.LOCAL, uri, description);
            mediaAdapter.add(media);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String[] autoWatermarks(){
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         return new String[]{String.format("拍摄时间：%s", sdf.format(new Date()))};
    }

    public enum MediaMenu {
        PICTURE_TAKE,
        PICTURE_CHOOSE,
        VIDEO_TAKE,
        VIDEO_CHOOSE
    }

    public interface OnMediaClickListener {
        void onMediaClick(int position);
    }

    public interface OnMediaChangeListener {
        void onMediaChange(List<Media> medias);
    }

    public interface OnViewMediaListener {
        void onViewMedia(int position);
    }

    private class MediaAdapter extends Adapter<BaseViewHolder> {

        private List<Media> medias;

        private OnMediaClickListener onMediaClickListener;

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BaseViewHolder holder = new BaseViewHolder(LayoutInflater.from(c).inflate(R.layout.adapter_media, parent, false));
            int size = DisplayUtil.getScreenWidth(c) - thisView.getPaddingStart() - thisView.getPaddingEnd() - DisplayUtil.dp2px(2) * 3;
            ViewUtil.setViewSize(holder.getView(R.id.iv_media), size / 4, size / 4);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            ImageView ivMedia = holder.getView(R.id.iv_media);
            ImageView ivVideoFlag = holder.getView(R.id.iv_video_flag);
            ImageView ivCross = holder.getView(R.id.iv_cross);
            switch (type) {
                case DEFAULT :
                    if (position == 0){
                        Glide.with(c).load(R.mipmap.icon_media_take).into(ivMedia);
                        ivCross.setVisibility(GONE);
                        ivVideoFlag.setVisibility(GONE);
                    } else {
                        Media media = medias.get(position - 1);
                        if (media.getSource() == Source.LOCAL) {
                            Glide.with(c).load(media.getUri()).into(ivMedia);
                        }
                        if (media.getSource() == Source.NETWORK) {
                            Glide.with(c).load(media.getUrl()).into(ivMedia);
                        }
                        ivVideoFlag.setVisibility(media.getMediaType() == MediaType.VIDEO ? VISIBLE : GONE);
                        ivCross.setVisibility(VISIBLE);
                    }
                    ivMedia.setOnClickListener(view -> {
                        if (onMediaClickListener != null) onMediaClickListener.onMediaClick(position - 1);
                    });
                    ivCross.setOnClickListener(view -> this.remove(position - 1));
                    break;
                case ONLY_VIEW:
                    Media media = medias.get(position);
                    if (media.getSource() == Source.LOCAL) {
                        Glide.with(c).load(media.getUri()).into(ivMedia);
                    }
                    if (media.getSource() == Source.NETWORK) {
                        Glide.with(c).load(media.getUrl()).into(ivMedia);
                    }

                    ivVideoFlag.setVisibility(media.getMediaType() == MediaType.VIDEO ? VISIBLE : GONE);
                    ivCross.setVisibility(GONE);
                    ivMedia.setOnClickListener(view -> {
                        if (onMediaClickListener != null) onMediaClickListener.onMediaClick(position);
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return type == DEFAULT ? (medias == null ? 1 : medias.size() + 1) : (medias == null ? 0 : medias.size());
        }

        public List<Media> getMedias() {
            return medias;
        }

        public void remove(int pos) {
            this.medias.remove(pos);
            update();
        }

        public void update(List<Media> medias) {
            this.medias = medias;
            update();
        }

        public void add(Media media) {
            if (this.medias == null) this.medias = new ArrayList<>();
            this.medias.add(media);
            update();
        }

        public void addAll(List<Media> medias) {
            if (this.medias == null) this.medias = new ArrayList<>();
            this.medias.addAll(medias);
            update();
        }

        private void update(){
            notifyDataSetChanged();
            if (onMediaChangeListener != null) onMediaChangeListener.onMediaChange(medias);
        }

        public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
            this.onMediaClickListener = onMediaClickListener;
        }
    }

}
