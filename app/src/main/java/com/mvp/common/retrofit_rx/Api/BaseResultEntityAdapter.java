package com.mvp.common.retrofit_rx.Api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanglin on 2017/8/30.
 */

public class BaseResultEntityAdapter implements JsonSerializer<BaseResultEntity>, JsonDeserializer<BaseResultEntity> {
    @Override
    public JsonElement serialize(BaseResultEntity src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        if (typeOfSrc instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)typeOfSrc;
            if (parameterizedType.getActualTypeArguments().length > 0) {
                Type t = parameterizedType.getActualTypeArguments()[0];
                result.add("data", new JsonPrimitive(new GsonBuilder().create().toJson(src.getData())));
            } else {
                result.add("data", new JsonPrimitive(src.getData().toString()));
            }
        } else {
            result.add("data", new JsonPrimitive(src.getData().toString()));
        }

        JsonObject reqResult = new JsonObject();
        reqResult.add("code", new JsonPrimitive(src.getErrorCode()));
        reqResult.add("msg", new JsonPrimitive(src.getErrorMsg()));
        result.add("reqResult", reqResult);
        return result;
    }

    @Override
    public BaseResultEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        BaseResultEntity entity = null;
        try{
            final JsonObject jsonObject = json.getAsJsonObject();
            entity = new BaseResultEntity() {
                @Override
                public String getErrorMsg() {
                    return jsonObject.getAsJsonObject("reqResult").get("msg").getAsString();
                }

                @Override
                public Integer getErrorCode() {
                    return jsonObject.getAsJsonObject("reqResult").get("code").getAsInt();
                }
            };
            if (typeOfT instanceof ParameterizedType && jsonObject != null && jsonObject.get("data") != null) {
                ParameterizedType parameterizedType = (ParameterizedType) typeOfT;
                if (parameterizedType.getActualTypeArguments().length > 0) {
                    Type t = parameterizedType.getActualTypeArguments()[0];
                    entity.setData(new GsonBuilder().create().fromJson(getElementData(jsonObject.get("data")), t));
                } else {
                    entity.setData(getElementData(jsonObject.get("data")));
                }
            } else {
                entity.setData(getElementData(jsonObject.get("data")));
            }
        } catch (Exception e) {
            entity = new BaseResultEntity() {
                @Override
                public String getErrorMsg() {
                    return "服务器开小差了，稍后再试";
                }

                @Override
                public Integer getErrorCode() {
                    return -999;
                }
            };
            entity.setData("");
        }

        return entity;
    }

    private String getElementData(JsonElement element) {
        if (element != null) {
            if (element instanceof JsonObject) {
                return  element.toString();
            } else if (element instanceof JsonElement) {
                return element.getAsString();
            } else {
                return element.toString();
            }
        }
        return null;
    }
}
