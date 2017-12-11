package com.mvp.common.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.mvp.common.R;

import java.util.Stack;

/**
 * Created by Allon on 2015/12/25.
 */
public class NavigationController {

    public static boolean isFragmentStatOk = true;
    private static NavigationController instance = null;
    private FragmentActivity activity;

    private FrameLayout layoutMain;

    private boolean isConfirmExit = false;//双击返回判断标志位
    private Stack<NavigationFragmentF> viewControllers = new Stack<NavigationFragmentF>();
    private Stack<NavigationFragmentF> canRemoveviewControllers = new Stack<NavigationFragmentF>();

    private NavigationController() {
    }

    public static synchronized NavigationController getInstance() {
        if (instance == null) {
            instance = new NavigationController();
        }
        return instance;
    }

    public void initNavigation(FragmentActivity activity, int mainFragmentId) {
        this.activity = activity;
        layoutMain = (FrameLayout) activity.findViewById(mainFragmentId);
    }

    public NavigationController pushViewController(NavigationFragmentF fragment, boolean animated) {
        return pushViewController(fragment, null, animated);
    }

    public NavigationController pushViewController(NavigationFragmentF fragment, Bundle data, boolean animated) {
        if (!viewControllers.empty()) {
            int pos = getFragmentPostion(viewControllers, fragment);
            if (pos == -1) {
                if (data != null) {
                    fragment.setArguments(data);
                }
                viewControllers.push(fragment);
            } else if (pos == viewControllers.size() - 1) {
                return instance;
            } else {
                int count = viewControllers.size();
                int x = pos;
                for (int i = count - 1; i > pos; i--) {
                    viewControllers.remove(i);
                }
                try {
                    if (data != null) {
                        viewControllers.get(x).setArguments(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewControllers.get(x).completionListener = fragment.completionListener;
            }
        } else {
            viewControllers.push(fragment);
        }
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (animated) {
            doInAnimation(ft);
        }
        ft.replace(layoutMain.getId(), viewControllers.peek());
//        if (fragment.isAdded()) {
//            ft.show(fragment);
//        } else {
//            ft.add(layoutMain.getId(), viewControllers.peek());
//        }

        ft.commitAllowingStateLoss();
        return instance;
    }

    public NavigationController popToViewController(NavigationFragmentF fragment, boolean animated) {
        return popToViewController(fragment, null, animated);
    }

    public NavigationController popToViewController(NavigationFragmentF fragment, Bundle data, boolean animated) {
        if (!viewControllers.empty()) {
            int pos = getFragmentPostion(viewControllers, fragment);
            if (pos == -1) {
                if (data != null) {
                    fragment.setArguments(data);
                }
                viewControllers.push(fragment);
            } else {
                int count = viewControllers.size();
                int x = pos;
                for (int i = count - 1; i > pos; i--) {
                    viewControllers.remove(i);
                }
                try {
                    if (data != null) {
                        viewControllers.get(x).setArguments(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewControllers.get(x).completionListener = fragment.completionListener;
            }
        } else {
            viewControllers.push(fragment);
        }
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (animated) {
            doOutAnimation(ft);
        }
        ft.replace(layoutMain.getId(), viewControllers.peek());
        ft.commitAllowingStateLoss();
        return instance;
    }

    public NavigationController popViewControllerAnimated(boolean animated) {
        return popViewControllerAnimated(null, animated);
    }

    public NavigationController popViewControllerAnimated(Bundle data, boolean animated) {
        if (!viewControllers.empty()) {
            viewControllers.pop();
            if (!viewControllers.empty()) {
                popToViewController(viewControllers.peek(), data, animated);
            }
        }
        return instance;
    }

    private void doInAnimation(FragmentTransaction ft) {
        isFragmentStatOk = false;
        ft.setCustomAnimations(getAnimationForAdd()[0], getAnimationForAdd()[1]);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isFragmentStatOk = true;
            }
        }, 400);
    }

    private void doOutAnimation(FragmentTransaction ft) {
        isFragmentStatOk = false;
        ft.setCustomAnimations(getAnimationForRemove()[0], getAnimationForRemove()[1]);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isFragmentStatOk = true;
            }
        }, 400);
    }

    public NavigationFragmentF getTopFragment() {
        if (!viewControllers.empty()) {
            return viewControllers.peek();
        }
        return null;
    }

    public NavigationFragmentF getWillBackFragment() {
        if (!viewControllers.empty()) {
            return viewControllers.get(viewControllers.size() - 2);
        }
        return null;
    }

    public NavigationController presentViewControllerAnimatedAndCompletion(NavigationFragmentF fragment, boolean animated, OnCompletionListener completion) {
        return presentViewControllerAnimatedAndCompletion(fragment, null, animated, completion);
    }

    public NavigationController presentViewControllerAnimatedAndCompletion(NavigationFragmentF fragment, Bundle data, boolean animated, OnCompletionListener completion) {
        fragment.completionListener = completion;
        if (!viewControllers.empty()) {
            int pos = getFragmentPostion(viewControllers, fragment);
            if (pos == -1) {
                pushViewController(fragment, data, animated);
                canRemoveviewControllers.push(fragment);
            } else if (pos == viewControllers.size() - 1) {
                return instance;
            } else {
                popToViewController(fragment, data, animated);
            }
        }
        return instance;
    }

    public NavigationController removePresentNavigationFragments() {
        if (!viewControllers.empty() && !canRemoveviewControllers.empty()) {
            for (NavigationFragmentF fragment : canRemoveviewControllers) {
                int pos = getFragmentPostion(viewControllers, fragment);
                if (pos != -1) {
                    viewControllers.remove(pos);
                }
            }
        }
        canRemoveviewControllers.removeAllElements();
        return instance;
    }

    public int getStackCount() {
        return viewControllers.size();
    }

    public void onDestroy() {
        viewControllers.clear();
        viewControllers = null;
        instance = null;
    }

    /**
     * fragment入栈动画效果
     *
     * @return 动画配置
     */
    private int[] getAnimationForAdd() {
        return new int[]{R.anim.common_slide_left_in, R.anim.common_slide_left_out};
    }

    /**
     * fragment出栈动画效果
     *
     * @return 动画配置
     */
    private int[] getAnimationForRemove() {
        return new int[]{R.anim.common_slide_right_in, R.anim.common_slide_right_out};
    }

    private int getFragmentPostion(Stack<NavigationFragmentF> stack, NavigationFragmentF obj) {
        for (int i = 0; i < stack.size(); i++) {
            if (obj.getClass().getName().equals(stack.get(i).getClass().getName())) {
                return i;
            }
        }
        return -1;
    }
}
