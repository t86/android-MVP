package com.mvp.common.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mvp.common.tools.ToastUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.subjects.PublishSubject;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

/**
 * fragment 基本父类
 */
public class BaseFragmentF extends NavigationFragmentF implements LifecycleProvider<FragmentEvent> {
    private final BehaviorProcessor<FragmentEvent> lifecycleSubject = BehaviorProcessor.create();
    PublishSubject<Integer> viewClickedSubject = null;
    Disposable disposable = null;


    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.toObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject.toObservable(), event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject.toObservable());
    }

    @Override
    @CallSuper
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        viewClickedSubject = PublishSubject.create();



        disposable = viewClickedSubject.throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer id) throws Exception {
                clickEvent(id);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                if (getActivity() != null) {
                    ToastUtils.toastShort(getActivity(), "很抱歉，程序有些异常，请稍后再试！");
                }
            }
        });
    }

    public static <T extends BaseFragmentF> T newInstance(Context context, Class<T> className) {
        Bundle args = new Bundle();
        T fragment = (T) T.instantiate(context, className.getCanonicalName());
        fragment.setArguments(args);
        fragment._mActivity = ((FragmentActivity) context);
        return fragment;
    }

    public <T extends BaseFragmentF> void startFragment(Class<T> fragmentClass) {
        startFragment(fragmentClass, new Bundle());
    }

    public <T extends BaseFragmentF> void startFragment(Class<T> fragmentClass, int launchMode) {
        startFragment(fragmentClass, new Bundle(), launchMode);
    }

    public <T extends BaseFragmentF> void startFragment(Class<T> fragmentClass, Bundle bundle) {
        if (_mActivity != null) {
            if (getParentFragment() != null && getParentFragment() instanceof SupportFragment) {
                startFragmentFromParent((SupportFragment)getParentFragment(),fragmentClass, bundle);
            } else {
                T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
                fragment.setArguments(bundle);
                start(fragment);
            }
        }
    }

    public <T extends BaseFragmentF> void startFragment(Class<T> fragmentClass, Bundle bundle, int launchMode) {
        if (_mActivity != null) {
            if (getParentFragment() != null && getParentFragment() instanceof SupportFragment) {
                startFragmentFromParent((SupportFragment) getParentFragment(), fragmentClass, bundle, launchMode);
            } else {
                T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
                fragment.setArguments(bundle);
                start(fragment, launchMode);
            }
        }
    }

    public <T extends BaseFragmentF> void startFragmentFromParent(Class<T> fragmentClass) {
        startFragmentFromParent(fragmentClass, new Bundle());
    }

    /**
     * 添加启动模式
     *
     * @param fragmentClass
     * @param launchMode    启动模式
     * @param <T>
     */
    public <T extends BaseFragmentF> void startFragmentFromParent(Class<T> fragmentClass, int launchMode) {
        startFragmentFromParent(fragmentClass, new Bundle(), launchMode);
    }

    public <T extends BaseFragmentF> void startFragmentFromParent(Class<T> fragmentClass, Bundle bundle) {
        T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
        fragment.setArguments(bundle);
        if (getParentFragment() != null && getParentFragment() instanceof SupportFragment) {
            startFragmentFromParent((SupportFragment) getParentFragment(), fragmentClass, bundle);
        } else {
            startFragment(fragmentClass, bundle);
        }
    }



    public <T extends BaseFragmentF> void startFragmentFromParent(Class<T> fragmentClass, Bundle bundle, int launchMode) {
        T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
        fragment.setArguments(bundle);
        if (getParentFragment() != null && getParentFragment() instanceof SupportFragment) {
            startFragmentFromParent((SupportFragment) getParentFragment(), fragmentClass, bundle, launchMode);
        } else {
            startFragment(fragmentClass, bundle, launchMode);
        }
    }

    public <T extends BaseFragmentF> void startFragmentFromParent(SupportFragment parentFragment, Class<T> fragmentClass, Bundle bundle) {
        T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
        fragment.setArguments(bundle);
        parentFragment.start(fragment);
    }

    /**
     * 添加启动模式
     *
     * @param parentFragment
     * @param fragmentClass
     * @param bundle
     * @param launchMode     启动模式
     * @param <T>
     */
    public <T extends BaseFragmentF> void startFragmentFromParent(SupportFragment parentFragment, Class<T> fragmentClass, Bundle bundle, int launchMode) {
        T fragment = BaseFragmentF.newInstance(_mActivity, fragmentClass);
        fragment.setArguments(bundle);
        parentFragment.start(fragment, launchMode);
    }


    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        setFragmentAnimator(new DefaultHorizontalAnimator());

    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        closeKeyboard();//界面销毁时关掉系统软键盘
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }


    @Override
    @CallSuper
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
        viewClickedSubject.onComplete();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
    }


    @Override
    public void onClick(final View view) {
        viewClickedSubject.onNext(view.getId());
    }

    protected void clickEvent(@IdRes Integer id) {
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    /**
     * 使状态栏透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }


}
