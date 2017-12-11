package com.mvp.common.tools.preferences;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

@TargetApi(HONEYCOMB)
public final class StringSetAdapter implements RealPreference.Adapter<Set<String>> {
  public static final StringSetAdapter INSTANCE = new StringSetAdapter();

  @Override public Set<String> get(@NonNull String key, @NonNull SharedPreferences preferences) {
    Set<String> value = preferences.getStringSet(key, null);
    return Collections.unmodifiableSet(value);
  }

  @Override public void set(@NonNull String key, @NonNull Set<String> value,
                            @NonNull SharedPreferences.Editor editor) {
    editor.putStringSet(key, value);
  }
}
