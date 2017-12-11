package com.mvp.common.tools;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvp.common.tools.preferences.BooleanAdapter;
import com.mvp.common.tools.preferences.ConverterAdapter;
import com.mvp.common.tools.preferences.EnumAdapter;
import com.mvp.common.tools.preferences.FloatAdapter;
import com.mvp.common.tools.preferences.IntegerAdapter;
import com.mvp.common.tools.preferences.LongAdapter;
import com.mvp.common.tools.preferences.Preference;
import com.mvp.common.tools.preferences.RealPreference;
import com.mvp.common.tools.preferences.StringAdapter;
import com.mvp.common.tools.preferences.StringSetAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static com.mvp.common.tools.preferences.Preconditions.checkNotNull;

/** A factory for reactive {@link Preference} objects. */
public final class RxSharedPreferences {
  private static final Float DEFAULT_FLOAT = 0f;
  private static final Integer DEFAULT_INTEGER = 0;
  private static final Boolean DEFAULT_BOOLEAN = false;
  private static final Long DEFAULT_LONG = 0L;
  private static final String DEFAULT_STRING = "";

  /** Create an instance of {@link RxSharedPreferences} for {@code com.zealfi.common.tools.preferences}. */
  @CheckResult
  @NonNull
  public static RxSharedPreferences create(@NonNull SharedPreferences preferences) {
    checkNotNull(preferences, "com.zealfi.common.tools.preferences == null");
    return new RxSharedPreferences(preferences);
  }

  private final SharedPreferences preferences;
  private final Observable<String> keyChanges;

  private RxSharedPreferences(final SharedPreferences preferences) {
    this.preferences = preferences;
    this.keyChanges = Observable.create(new ObservableOnSubscribe<String>() {
      @Override public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
        final OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
          @Override
          public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
            emitter.onNext(key);
          }
        };

        emitter.setCancellable(new Cancellable() {
          @Override public void cancel() throws Exception {
            preferences.unregisterOnSharedPreferenceChangeListener(listener);
          }
        });

        preferences.registerOnSharedPreferenceChangeListener(listener);
      }
    }).share();
  }

  /** Create a boolean preference for {@code key}. Default is {@code false}. */
  @CheckResult
  @NonNull
  public Preference<Boolean> getBoolean(@NonNull String key) {
    return getBoolean(key, DEFAULT_BOOLEAN);
  }

  /** Create a boolean preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public Preference<Boolean> getBoolean(@NonNull String key, @NonNull Boolean defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, BooleanAdapter.INSTANCE, keyChanges);
  }

  /** Create an enum preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public <T extends Enum<T>> Preference<T> getEnum(@NonNull String key, @NonNull T defaultValue,
                                                   @NonNull Class<T> enumClass) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    checkNotNull(enumClass, "enumClass == null");
    return new RealPreference<>(preferences, key, defaultValue, new EnumAdapter<>(enumClass), keyChanges);
  }

  /** Create a float preference for {@code key}. Default is {@code 0}. */
  @CheckResult
  @NonNull
  public Preference<Float> getFloat(@NonNull String key) {
    return getFloat(key, DEFAULT_FLOAT);
  }

  /** Create a float preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public Preference<Float> getFloat(@NonNull String key, @NonNull Float defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, FloatAdapter.INSTANCE, keyChanges);
  }

  /** Create an integer preference for {@code key}. Default is {@code 0}. */
  @CheckResult
  @NonNull
  public Preference<Integer> getInteger(@NonNull String key) {
    //noinspection UnnecessaryBoxing
    return getInteger(key, DEFAULT_INTEGER);
  }

  /** Create an integer preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public Preference<Integer> getInteger(@NonNull String key, @NonNull Integer defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, IntegerAdapter.INSTANCE, keyChanges);
  }

  /** Create a long preference for {@code key}. Default is {@code 0}. */
  @CheckResult
  @NonNull
  public Preference<Long> getLong(@NonNull String key) {
    //noinspection UnnecessaryBoxing
    return getLong(key, DEFAULT_LONG);
  }

  /** Create a long preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public Preference<Long> getLong(@NonNull String key, @NonNull Long defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, LongAdapter.INSTANCE, keyChanges);
  }

  /**
   * Create a preference for type {@code T} for {@code key} with a default of {@code defaultValue}.
   */
  @CheckResult
  @NonNull
  public <T> Preference<T> getObject(@NonNull String key,
                                     @NonNull T defaultValue, @NonNull Preference.Converter<T> converter) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    checkNotNull(converter, "converter == null");
    return new RealPreference<>(preferences, key, defaultValue,
        new ConverterAdapter<>(converter), keyChanges);
  }

  /** Create a string preference for {@code key}. Default is {@code ""}. */
  @CheckResult
  @NonNull
  public Preference<String> getString(@NonNull String key) {
    return getString(key, DEFAULT_STRING);
  }

  /** Create a string preference for {@code key} with a default of {@code defaultValue}. */
  @CheckResult
  @NonNull
  public Preference<String> getString(@NonNull String key, @NonNull String defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, StringAdapter.INSTANCE, keyChanges);
  }

  /**
   * Create a string set preference for {@code key}. Default is an empty set. Note that returned set
   * value will always be unmodifiable.
   */
  @RequiresApi(HONEYCOMB)
  @CheckResult
  @NonNull
  public Preference<Set<String>> getStringSet(@NonNull String key) {
    return getStringSet(key, Collections.<String>emptySet());
  }

  /** Create a string set preference for {@code key} with a default of {@code defaultValue}. */
  @RequiresApi(HONEYCOMB)
  @CheckResult
  @NonNull
  public Preference<Set<String>> getStringSet(@NonNull String key,
                                              @NonNull Set<String> defaultValue) {
    checkNotNull(key, "key == null");
    checkNotNull(defaultValue, "defaultValue == null");
    return new RealPreference<>(preferences, key, defaultValue, StringSetAdapter.INSTANCE, keyChanges);
  }
  
  public void clear() {
    preferences.edit().clear().apply();
  }

  public  <T> void setEntity(@NonNull String key, final Class<T> cls, T data){

    T newStance = null;
    try {
        newStance = (cls.newInstance());
        getObject(key, newStance, new Preference.Converter<T>() {
            @NonNull
            @Override
            public T deserialize(@NonNull String s) {
                return new Gson().fromJson(s, cls);
            }

            @NonNull
            @Override
            public String serialize(@NonNull T strings) {
                return new Gson().toJson(strings, cls);
            }
        }).set(data);

    } catch (InstantiationException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    }

  }

    public  <T> void setEntity(@NonNull String key, List<T> data){

        List<T> newStance = new ArrayList<T>();
        getObject(key, newStance, new Preference.Converter<List<T> >() {
            @NonNull
            @Override
            public List<T> deserialize(@NonNull String s) {
                return new Gson().fromJson(s, new TypeToken<List<T>>(){}.getType());
            }

            @NonNull
            @Override
            public String serialize(@NonNull List<T>  strings) {
                return new Gson().toJson(strings, new TypeToken<List<T>>(){}.getType());
            }
        }).set(data);

    }

    public <K, V> void setEntity(@NonNull String key, Map<K,V> data){
        Map<K,V> newStance = new HashMap<>();
        getObject(key, newStance, new Preference.Converter<Map<K,V>>() {
            @NonNull
            @Override
            public Map<K,V> deserialize(@NonNull String s) {
                return new Gson().fromJson(s, new TypeToken<Map<K,V>>(){}.getType());
            }

            @NonNull
            @Override
            public String serialize(@NonNull Map<K,V> strings) {
                return new Gson().toJson(strings, new TypeToken<Map<K,V>>(){}.getType());
            }
        }).set(data);
    }

    public  <T> List<T> getEntityList(@NonNull String key){

        List<T> newStance =  getObject(key, new ArrayList<T>(), new Preference.Converter<List<T>>() {
            @NonNull
            @Override
            public List<T> deserialize(@NonNull String s) {
                return new Gson().fromJson(s, new TypeToken<List<T>>(){}.getType());
            }

            @NonNull
            @Override
            public String serialize(@NonNull List<T> strings) {
                return new Gson().toJson(strings, new TypeToken<List<T>>(){}.getType());
            }
        }).get();

        return newStance;
    }

    public  <K,V> Map<K,V> getEntityMap(@NonNull String key){

        Map<K,V> newStance =  getObject(key, new HashMap<K,V>(), new Preference.Converter<Map<K,V>>() {
            @NonNull
            @Override
            public Map<K,V> deserialize(@NonNull String s) {
                return new Gson().fromJson(s, new TypeToken<Map<K,V>>(){}.getType());
            }

            @NonNull
            @Override
            public String serialize(@NonNull Map<K,V> strings) {
                return new Gson().toJson(strings, new TypeToken<Map<K,V>>(){}.getType());
            }
        }).get();

        return newStance;
    }


    public  <T> T getEntity(@NonNull String key, final Class<T> cls){

        T newStance = null;
        try {
            newStance = getObject(key, cls.newInstance(), new Preference.Converter<T>() {
                @NonNull
                @Override
                public T deserialize(@NonNull String s) {
                    return new Gson().fromJson(s, cls);
                }

                @NonNull
                @Override
                public String serialize(@NonNull T strings) {
                    return new Gson().toJson(strings, cls);
                }
            }).get();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return newStance;
    }


}
