package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public final class LongAdapter implements RealPreference.Adapter<Long> {
  public static final LongAdapter INSTANCE = new LongAdapter();

  @Override public Long get(@NonNull String key, @NonNull SharedPreferences preferences) {
    return preferences.getLong(key, 0L);
  }

  @Override public void set(@NonNull String key, @NonNull Long value,
                            @NonNull SharedPreferences.Editor editor) {
    editor.putLong(key, value);
  }
}
