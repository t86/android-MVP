package com.mvp.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mvp.common.tools.TimerManager;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by Allon on 2015/12/28.
 */
public class NavigationFragmentF extends SupportFragment implements View.OnClickListener, View.OnTouchListener {
    public OnCompletionListener completionListener = null;
    public String pageName = getClass().getName();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeKeyboard();
//        setAllViewOnTouchListener((ViewGroup) view);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        TimerManager.getInstance().destroyTimerByTag(getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }


    @Override
    public void onClick(View v) {
        closeKeyboard();
    }

    private void setAllViewOnTouchListener(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setAllViewOnTouchListener((ViewGroup) view);
            } else {
                view.setOnTouchListener(this);
            }
        }
    }

    /**
     * 设置页面顶部标题
     *
     * @param textId 标题内容资源id
     */
    protected void setPageTitle(int textId) {
        pageName = _mActivity.getResources().getString(textId);
    }


    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        if ((arg0 instanceof EditText) && arg0.isEnabled()) {
            return false;
        }
        View currentFocusView = getActivity().getCurrentFocus();
        if (currentFocusView != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    public boolean onGoBack() {
        closeKeyboard();
        if (NavigationController.getInstance().getStackCount() == 1) {
            return false;
        }
        return true;
    }

    public void closeKeyboard() {
        if (getActivity() == null) {
            return;
        }
        View currentFocusView = getActivity().getCurrentFocus();
        if (currentFocusView != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 方法一(如果输入法在窗口上已经显示，则隐藏，反之则显示)
     */
    public void closeKeyboardF() {
        if (getActivity() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setThemeColor(View rootView, int color) {

    }


}
