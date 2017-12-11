package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public final class FloatAdapter implements RealPreference.Adapter<Float> {
  public static final FloatAdapter INSTANCE = new FloatAdapter();

  @Override public Float get(@NonNull String key, @NonNull SharedPreferences preferences) {
    return preferences.getFloat(key, 0f);
  }

  @Override public void set(@NonNull String key, @NonNull Float value,
                            @NonNull SharedPreferences.Editor editor) {
    editor.putFloat(key, value);
  }
}
