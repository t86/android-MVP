package com.mvp.common.retrofit_rx.Api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.mvp.common.BaseActivity;
import com.mvp.common.fragment.BaseFragmentF;
import com.mvp.common.retrofit_rx.RxRetrofitApp;
import com.mvp.common.retrofit_rx.http.HttpManager;
import com.mvp.common.retrofit_rx.http.cookie.CookieInterceptor;
import com.mvp.common.retrofit_rx.listener.HttpOnNextListener;
import com.mvp.common.retrofit_rx.utils.AppSession;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求数据统一封装类
 * Created by WZG on 2016/7/16.
 */
public abstract class BaseApi<T> implements Function<BaseResultEntity<T>, T> {
    //rx生命周期管理
    protected BaseFragmentF mBaseFragmentF;
    /*回调*/
    private HttpOnNextListener listener;
    /*是否能取消加载框*/
    private boolean cancel;
    /*是否显示加载框*/
    private boolean showProgress;
    /*是否需要缓存处理*/
    private boolean cache;
    /*方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    private String path;
    /*超时时间-默认6秒*/
    private int connectionTime = 6;
    /*有网情况下的本地缓存时间默认60秒*/
    private int cookieNetWorkTime = 60;
    /*无网络的情况下本地缓存时间默认30天*/
    private int cookieNoNetWorkTime = 24 * 60 * 60 * 30;
    /* 失败后retry次数*/
    private int retryCount = 1;
    /*失败后retry延迟*/
    private long retryDelay = 100;
    /*失败后retry叠加延迟*/
    private long retryIncreaseDelay = 10;
    private HashMap<String, String> mParams = null;
    protected boolean hideErrorToast;
    protected Retrofit retrofit;

    private BaseActivity mActivity;
    private Type dataType;

    public BaseApi(HttpOnNextListener listener, BaseFragmentF fragment) {
        setRxAppFragment(fragment);
        init(listener);
    }

    public BaseApi(HttpOnNextListener listener, BaseActivity ac) {
        setActivity(ac);
        init(listener);
    }

    private void init(HttpOnNextListener listener){
        setListener(listener);
        setShowProgress(true);
        setCookieNetWorkTime(60);
        setCookieNoNetWorkTime(24 * 60 * 60);
        setCache(false);
    }

    /**
     * 设置参数
     */
    public abstract Observable getObservable();

    public abstract Interceptor headerInterceptor();

    public abstract Interceptor bodyInterceptor();

    protected abstract void setParams();

    public int getCookieNoNetWorkTime() {
        return cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getPath() {
        return path;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return AppSession.getInstance().getBaseUrl() + path;
    }

    public void setRxAppFragment(BaseFragmentF fragment) {
        this.mBaseFragmentF = fragment;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public HttpOnNextListener  getListener() {
        return listener;
    }

    public void setListener(HttpOnNextListener listener) {
        this.listener = listener;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public long getRetryIncreaseDelay() {
        return retryIncreaseDelay;
    }

    public void setRetryIncreaseDelay(long retryIncreaseDelay) {
        this.retryIncreaseDelay = retryIncreaseDelay;
    }

    public BaseActivity getActivity() {
        if (mBaseFragmentF != null) {
            return (BaseActivity) mBaseFragmentF.getActivity();
        }
        return mActivity;
    }

    public void setActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    /*
     * 获取当前rx生命周期
     * @return
     */
    public LifecycleTransformer getTransformer() {
        if (mBaseFragmentF != null) {
            return mBaseFragmentF.bindToLifecycle();
        } else {
            return mActivity.bindToLifecycle();
        }
    }

//    /*
// * 获取当前rx生命周期
// * @return
// */
//    public LifecycleTransformer getTransformer() {
//        return mBaseFragmentF.getActivity();
//    }

    @Override
    public BaseResultEntity<T> apply(BaseResultEntity httpResult) {

        return httpResult;
    }

    public void execute() {
        HttpManager manager = HttpManager.getInstance();
        setParams();
        manager.doHttpDeal(this);
    }

    public HashMap<String, String> getParams() {
        if (mParams == null) {
            setParams();
            if (mParams != null) {
                mParams.values().removeAll(Collections.singleton(null));
            }
        }
        return mParams;
    }

    public void setParams(HashMap<String, String> mParams) {
        this.mParams = mParams;
        this.mParams.values().removeAll(Collections.singleton(null));
    }

    //重新登陆相关
    public abstract void restartLogin(Context context, int errorCode, String errorMessage);

    //登录时提醒零花钱用户
    public abstract void openGuidePage(int code);

    public void setRetrofit(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(getConnectionTime(), TimeUnit.SECONDS);
        builder.addInterceptor(new CookieInterceptor(isCache(), getUrl()));
        if (RxRetrofitApp.isDebug()) {
            builder.addInterceptor(getHttpLoggingInterceptor());
        }
        builder.addInterceptor(headerInterceptor());
        builder.addInterceptor(bodyInterceptor());

        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser).create();
        /*创建retrofit对象*/
        this.retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AppSession.getInstance().getBaseUrl())
                .build();
    }

    public BaseResultEntityAdapter getAdapter() {
        return new BaseResultEntityAdapter();
    }

    public boolean isHideErrorToast() {
        return hideErrorToast;
    }

    /**
     * 日志输出
     * 自行判定是否添加
     *
     * @return
     */
    protected HttpLoggingInterceptor getHttpLoggingInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("RxRetrofit", "Retrofit====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

}
