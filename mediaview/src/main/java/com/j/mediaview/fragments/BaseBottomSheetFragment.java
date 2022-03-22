package com.j.mediaview.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.j.mediaview.R;
import com.j.mediaview.utils.DisplayUtil;

public class BaseBottomSheetFragment extends BottomSheetDialogFragment {

    public Context mContext;
    public Activity mActivity;

    private BottomSheetBehavior<FrameLayout> behavior;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = requireContext();
        mActivity = requireActivity();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null){
            FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = DisplayUtil.getScreenHeight(mContext);
                behavior = BottomSheetBehavior.from(bottomSheet);
                // 初始为展开状态
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }
}