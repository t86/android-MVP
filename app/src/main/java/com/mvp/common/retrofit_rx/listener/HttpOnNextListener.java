package com.mvp.common.retrofit_rx.listener;


import com.mvp.common.retrofit_rx.Api.BaseResultEntity;

import io.reactivex.Observable;

/**
 * 成功回调处理
 * Created by WZG on 2016/7/16.
 */
public abstract class HttpOnNextListener<T extends BaseResultEntity<R>, R> {

    public abstract void onNext(R r);

    /**
     * 成功后回调方法
     * @param t
     */
    public void onNext(T t){
        R a = t.getData();
        onNext(a);
    }

    /**
     * 緩存回調結果
     * @param result
     */
    public void onCacheNext(String result){
    }

    /**
     * 成功后的ober返回，扩展链接式调用
     * @param observable
     */
    public void onNext(Observable observable){

    }

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     * @param e
     */
    public  void onError(Throwable e){

    }

    /**
     * 取消回調
     */
    public void onCancel(){

    }


}
