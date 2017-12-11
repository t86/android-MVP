package com.mvp.common.retrofit_rx.Api;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

/**
 * 回调信息统一封装类
 * Created by WZG on 2016/7/16.
 */
public abstract class BaseResultEntity<T> {

    public BaseResultEntity() {

    }
    /**
     * 请求结果
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 数据
     */
    private T data;

    public abstract String getErrorMsg();

    public abstract Integer getErrorCode();

    public static Type getType(final Class<?> rawClass,final Type parameter) {
        if (parameter != null) {
            return new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return new Type[]{parameter};
                }

                @Override
                public Type getRawType() {
                    return rawClass;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            };
        } else {
            return rawClass;
        }
    }
}
