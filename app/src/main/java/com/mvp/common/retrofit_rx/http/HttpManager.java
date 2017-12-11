package com.mvp.common.retrofit_rx.http;

import com.mvp.common.retrofit_rx.Api.BaseApi;
import com.mvp.common.retrofit_rx.http.exception.RetryWhenNetworkException;
import com.mvp.common.retrofit_rx.listener.HttpOnNextListener;
import com.mvp.common.retrofit_rx.subscribers.ProgressSubscriber;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * http交互处理类
 * Created by WZG on 2016/7/16.
 */
public class HttpManager {
    private volatile static HttpManager INSTANCE;

    //构造方法私有
    private HttpManager() {
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void doHttpDeal(BaseApi basePar) {

        Observable observable = httpObservable(basePar);

        /*数据回调*/
        Flowable flowable = observable.toFlowable(BackpressureStrategy.LATEST);
        flowable.subscribe(new ProgressSubscriber(basePar));

    }

    public Observable httpObservable(BaseApi basePar) {
        return new CreateRequestObervable(basePar).invoke().getObservable();
    }

    public void subscribe(Observable observable, BaseApi basePar) {
        Flowable flowable = observable.toFlowable(BackpressureStrategy.LATEST);
        flowable.subscribe(new ProgressSubscriber(basePar));
    }

    private class CreateRequestObervable {
        private BaseApi basePar;
        private Observable observable;

        public CreateRequestObervable(BaseApi basePar) {
            this.basePar = basePar;
        }

        public Observable getObservable() {
            return observable;
        }

        public CreateRequestObervable invoke() {
            long startTime = System.currentTimeMillis();
            //手动创建一个OkHttpClient并设置超时时间缓存等设置

        /*rx处理*/
            basePar.setRetrofit();
            observable = basePar.getObservable()
                 /*失败后的retry配置*/
                    .retryWhen(new RetryWhenNetworkException(basePar.getRetryCount(),
                            basePar.getRetryDelay(), basePar.getRetryIncreaseDelay()))
                /*生命周期管理*/
                    .compose(basePar.getTransformer())
                /*http请求线程*/
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                    .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                    .map(basePar);

        /*链接式对象返回*/
            HttpOnNextListener httpOnNextListener = basePar.getListener();
            if (httpOnNextListener != null) {
                httpOnNextListener.onNext(observable);
            }
            return this;
        }
    }


}
