package com.mvp.common.retrofit_rx.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 方法工具类
 * Created by WZG on 2016/10/31.
 */

/**
 * 注意！！！
 * 应用启动时需要在Application中调用AppSession的init方法
 */

public class AppSession {
    private String baseUrl;
    private Context appContext;
    private String channelId;
    private boolean realSend;
    private boolean realCheck;

    private static AppSession instance;


    private AppSession(Context appContext,String url, String channelId){
        this.appContext = appContext;
        this.baseUrl = url;
        this.channelId = channelId;
    }

    public static synchronized void init(Context appContext,String url, String channelId) {
        if (instance == null) {
            instance = new AppSession(appContext, url, channelId);
        }
    }

    public static synchronized AppSession getInstance() {
        if (instance == null) {
            throw new RuntimeException("AppSession is not init!!!");
        }
        return instance;
    }

    /**
     * 读取baseurl
     * @param url
     * @return
     */
    public String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }

    /**
     * 保存baseurl
     */
    public void setBaseUrl(String url) {
        baseUrl = url;
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    public void setAppContext(Context context) {
        appContext = context;
    }

    public Context getAppContext(){
        if (appContext == null) {
            throw new NullPointerException();
        }
        return appContext;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isRealSend() {
        return realSend;
    }

    public void setRealSend(boolean realSend) {
        this.realSend = realSend;
    }

    public boolean isRealCheck() {
        return realCheck;
    }

    public void setRealCheck(boolean realCheck) {
        this.realCheck = realCheck;
    }

    public PackageInfo getPackageInfo() {
        try {
            return appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
