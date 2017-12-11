package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by tanglin on 2017/6/28.
 */

public class GsonPreferenceAdapter<T> implements RealPreference.Adapter<T> {
    final Gson gson = new GsonBuilder().create();
    private Class<T> clazz;

    // Constructor and exception handling omitted for brevity.

    @Override
    public T get(String key, SharedPreferences preferences) {
        return gson.fromJson(preferences.getString(key, ""), clazz);
    }

    @Override
    public void set(String key, T value, SharedPreferences.Editor editor) {
        editor.putString(key, gson.toJson(value));
    }
}
