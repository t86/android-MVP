package com.mvp.common.tools.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.mvp.common.tools.preferences.Preconditions.checkNotNull;

public final class ConverterAdapter<T> implements RealPreference.Adapter<T> {
  private final Preference.Converter<T> converter;

  public ConverterAdapter(Preference.Converter<T> converter) {
    this.converter = converter;
  }

  @Override public T get(@NonNull String key, @NonNull SharedPreferences preferences) {
    String serialized = preferences.getString(key, "");
    T value = converter.deserialize(serialized);
    checkNotNull(value, "Deserialized value must not be null from string: " + serialized);
    return value;
  }

  @Override
  public void set(@NonNull String key, @NonNull T value, @NonNull SharedPreferences.Editor editor) {
    String serialized = converter.serialize(value);
    checkNotNull(serialized, "Serialized string must not be null from value: " + value);
    editor.putString(key, serialized);
  }
}
