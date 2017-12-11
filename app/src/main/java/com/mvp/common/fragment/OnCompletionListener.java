package com.mvp.common.fragment;

/**
 * Created by Allon on 2015/12/28.
 */
public interface OnCompletionListener {
    void completPopToViewControllerAnimated(boolean animated);

    void completPopToViewControllerAnimated(boolean animated, Object obj);
}