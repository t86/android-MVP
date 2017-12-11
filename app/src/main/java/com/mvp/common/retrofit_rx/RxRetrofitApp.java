package com.mvp.common.retrofit_rx;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mvp.common.tools.RxSharedPreferences;

import java.util.HashMap;


/**
 * 全局app
 * Created by WZG on 2016/12/12.
 */

public class RxRetrofitApp  {
    private static Application application;
    private static boolean debug;
    private static HashMap<String, RxSharedPreferences>  rxPrefs;


    public static void init(Application app){
        init(app, true);
    }

    public static void init(Application app,boolean debug){
        setApplication(app);
        setDebug(debug);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
        rxPrefs = new HashMap<>();
        rxPrefs.put("", RxSharedPreferences.create(preferences));
    }

    public static Application getApplication() {
        return application;
    }

    private static void setApplication(Application application) {
        RxRetrofitApp.application = application;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        RxRetrofitApp.debug = debug;
    }

    public static RxSharedPreferences getRxPrefs(String name){
        if (!rxPrefs.containsKey(name)){
            SharedPreferences preferences = RxRetrofitApp.application.getSharedPreferences(name, Context.MODE_PRIVATE);
            RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(preferences);
            rxPrefs.put(name, rxSharedPreferences);
        }
        return rxPrefs.get(name);
    }

    public static RxSharedPreferences getRxPrefs(){
        return rxPrefs.get("");
    }
}
