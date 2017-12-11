package com.mvp.common.retrofit_rx.subscribers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.mvp.common.BaseActivity;
import com.mvp.common.R;
import com.mvp.common.retrofit_rx.Api.BaseApi;
import com.mvp.common.retrofit_rx.Api.BaseResultEntity;
import com.mvp.common.retrofit_rx.RxRetrofitApp;
import com.mvp.common.retrofit_rx.http.cookie.CookieResulte;
import com.mvp.common.retrofit_rx.http.exception.HttpTimeException;
import com.mvp.common.retrofit_rx.listener.HttpOnNextListener;
import com.mvp.common.retrofit_rx.utils.AppSession;
import com.mvp.common.retrofit_rx.utils.CookieDbUtil;
import com.mvp.common.tools.NetWorkUtils;
import com.mvp.common.tools.ToastUtils;

import java.lang.ref.SoftReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T extends BaseResultEntity> extends ResourceSubscriber<T> {
    /*是否弹框*/
    private boolean showPorgress = true;
    /* 软引用回调接口*/
    private HttpOnNextListener mSubscriberOnNextListener;
    /*加载框可自己定义*/
    private ProgressDialog pd;
    /*请求数据*/
    private BaseApi api;
    private SoftReference<BaseActivity> mActivityRef;

    private Type dataType;

    PublishSubject<String> toastSubject = null;
    Disposable disposable = null;
    private String lastMsg = "";

    /**
     * 构造
     *
     * @param api
     */
    public ProgressSubscriber(final BaseApi api) {
        this.api = api;
        this.mSubscriberOnNextListener = api.getListener();
        if (mSubscriberOnNextListener != null) {
            if (this.mSubscriberOnNextListener.getClass().getGenericSuperclass() instanceof ParameterizedType) {
                dataType = ((ParameterizedType) this.mSubscriberOnNextListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
        }
        mActivityRef = new SoftReference(api.getActivity());
        setShowPorgress(api.isShowProgress());
        if (api.isShowProgress()) {
            initProgressDialog(api.isCancel());
        }

        toastSubject = PublishSubject.create();
        disposable = toastSubject.throttleFirst(3, TimeUnit.SECONDS).filter(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String s) throws Exception {
                if (lastMsg.equals(s)){
                    return false;
                }
                return true;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String msg) throws Exception {
                lastMsg = msg;
                if (!api.isHideErrorToast()) {
                    Activity activity = mActivityRef.get();
                    if (activity != null) {
                        ToastUtils.toastShort(activity, msg);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                if (mActivityRef.get() != null) {
                    ToastUtils.toastShort(mActivityRef.get(), "很抱歉，程序有些异常，请稍后再试！");
                }
            }
        });
    }


    /**
     * 初始化加载框
     */
    private void initProgressDialog(boolean cancel) {
        if (pd == null && this.mActivityRef.get() != null) {
            pd = new ProgressDialog(this.mActivityRef.get());
            pd.setCancelable(cancel);
            pd.setMessage("数据加载中......");
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mSubscriberOnNextListener != null) {
                            mSubscriberOnNextListener.onCancel();
                        }
                        onCancelProgress();
                    }
                });
            }
        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (!isShowPorgress()) return;
        if (pd == null || this.mActivityRef.get() == null) return;
        if (!pd.isShowing()) {
            pd.show();
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (!isShowPorgress()) return;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = null;
        this.dispose();

    }



    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        /*需要緩存并且本地有缓存才返回*/
        if (api.isCache()) {
            Observable.just(api.getUrl()).subscribe(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    /*获取缓存数据*/
                    CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(s);
                    if (cookieResulte == null) {
                        throw new HttpTimeException("网络错误");
                    }
                    long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                    if (time < api.getCookieNoNetWorkTime()) {
                        if (mSubscriberOnNextListener != null) {
                            GsonBuilder gson = new GsonBuilder();
                            gson.registerTypeAdapter(BaseResultEntity.class, api.getAdapter());
                            mSubscriberOnNextListener.onNext((BaseResultEntity) gson.create().fromJson(cookieResulte.getResulte(), BaseResultEntity.getType(BaseResultEntity.class, dataType)));
                            ProgressSubscriber.this.onComplete();
                        }
                    } else {
                        CookieDbUtil.getInstance().deleteCookie(cookieResulte);
                        throw new HttpTimeException("网络错误");
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    errorDo(throwable);
                }
            });
        } else {
            errorDo(e);
        }

    }


    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    /*错误统一处理*/
    private void errorDo(Throwable e) {
        if (this.mActivityRef.get() == null) return;
        Activity activity = mActivityRef.get();
        String errmsg = "";
        if (e instanceof SocketTimeoutException) {
            errmsg = "网络中断，请检查您的网络状态";
            toastSubject.onNext(errmsg);
        } else if (e instanceof ConnectException) {
            errmsg = "网络中断，请检查您的网络状态";
            toastSubject.onNext(errmsg);
        } else if (e instanceof HttpTimeException) {
            errmsg = e.getMessage();
            if (!TextUtils.isEmpty(errmsg)) {
                toastSubject.onNext(errmsg);
            }
        } else {
            if (!NetWorkUtils.isNetworkEnable(AppSession.getInstance().getAppContext())) {
                errmsg = activity.getString(R.string.common_network_error);
                toastSubject.onNext(errmsg);
            } else {
                errmsg = activity.getString(R.string.common_server_error);
                toastSubject.onNext(errmsg);
            }
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(new Throwable(errmsg));
            this.onComplete();
        }
    }


    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
        onComplete();
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isDisposed()) {
            this.dispose();
        }
    }


    public boolean isShowPorgress() {
        return showPorgress;
    }

    /**
     * 是否需要弹框设置
     *
     * @param showPorgress
     */
    public void setShowPorgress(boolean showPorgress) {
        this.showPorgress = showPorgress;
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog();
        /*缓存并且有网*/
        if (api.isCache() && NetWorkUtils.isNetworkAvailable(RxRetrofitApp.getApplication())) {
            try {
         /*获取缓存数据*/
                CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
                if (cookieResulte != null) {
                    long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                    if (time < api.getCookieNetWorkTime()) {
                        if (mSubscriberOnNextListener != null) {
                            GsonBuilder gson = new GsonBuilder();
                            gson.registerTypeAdapter(BaseResultEntity.class, api.getAdapter());
                            mSubscriberOnNextListener.onNext((BaseResultEntity) gson.create().fromJson(cookieResulte.getResulte(), BaseResultEntity.getType(BaseResultEntity.class, dataType)));
                            this.onComplete();
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}