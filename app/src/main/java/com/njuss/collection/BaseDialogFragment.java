package com.njuss.collection;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.njuss.collection.tools.ScreenUtils;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseDialogFragment extends DialogFragment {

    protected Context mContext;
    protected View mView;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_recordDialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //dialog基本设置

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);// 是否可以按“返回键”消失
        getDialog().setCanceledOnTouchOutside(false); // 点击加载框以外的区域
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        mView = inflater.inflate(setView(), container, false);






        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            if (lp != null) {

                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();//背景变暗
                lp1.alpha = 0.5f;
                lp1.dimAmount = 0.5f;
                getActivity().getWindow().setAttributes(lp1);

                lp.gravity = Gravity.CENTER;
                lp.width = ScreenUtils.getScreenWidth(mContext);
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
//                    window.setWindowAnimations(R.style.menu_anim_style);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract int setView();

}
