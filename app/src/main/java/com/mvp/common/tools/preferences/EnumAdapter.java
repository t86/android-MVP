package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public final class EnumAdapter<T extends Enum<T>> implements RealPreference.Adapter<T> {
  private final Class<T> enumClass;

  public EnumAdapter(Class<T> enumClass) {
    this.enumClass = enumClass;
  }

  @Override public T get(@NonNull String key, @NonNull SharedPreferences preferences) {
    String value = preferences.getString(key, null);
    return Enum.valueOf(enumClass, value);
  }

  @Override
  public void set(@NonNull String key, @NonNull T value, @NonNull SharedPreferences.Editor editor) {
    editor.putString(key, value.name());
  }
}
