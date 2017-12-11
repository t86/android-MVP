package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public final class StringAdapter implements RealPreference.Adapter<String> {
  public static final StringAdapter INSTANCE = new StringAdapter();

  @Override public String get(@NonNull String key, @NonNull SharedPreferences preferences) {
    String value = preferences.getString(key, null);
    return value;
  }

  @Override public void set(@NonNull String key, @NonNull String value,
                            @NonNull SharedPreferences.Editor editor) {
    editor.putString(key, value);
  }
}
