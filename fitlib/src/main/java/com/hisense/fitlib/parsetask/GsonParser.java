package com.hisense.fitlib.parsetask;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class GsonParser<T> extends Parser<Void, ParseResult> {

    private final Gson gson;
    private Type resultType;

    public GsonParser(ParseListener parseListener, String jsonString, Gson gson) {
        super(parseListener, jsonString);
        this.gson = gson;
        Type[] genericInterfaces = getClass().getGenericInterfaces();
                Log.w("GsonParser", "parse genericInterfaces = " + genericInterfaces.length);

        resultType = new TypeToken<T>(){}.getType();
/*
        Type genericSuperclass = getClass().getGenericSuperclass();
                Log.w("GsonParser", "parse genericSuperclass = " + genericSuperclass);
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                Log.w("GsonParser", "parse actualTypeArguments = " + actualTypeArguments.length);
        Type actualTypeArgument = actualTypeArguments[0];
        if (actualTypeArgument instanceof Class<?>) {
            // type is a normal class.
            Class<?> actualTypeArgument1 = (Class<?>) actualTypeArgument;
            resultType = (Class<? super T>) actualTypeArgument1;
        }*/
                Log.w("GsonParser", "parse resultType = " + resultType);

    }

    public GsonParser(Context context, ParseListener parseListener, String assetName, Gson gson, Class clazz) {
        super(context, parseListener, assetName);
        this.gson = gson;
//        this.resultType = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        resultType = new TypeToken<T>(){}.getType();
                Log.w("GsonParser", "parse resultType = " + resultType);
    }

    @Override
    protected ParseResult parseAsset(Context mContext, String mAssertName) {
        return parse(FileUtil.readAssetsFile(mContext, mAssertName));
    }

    @Override
    protected ParseResult parse(String json) {
        try {
            /*long startTime = System.nanoTime();
            T o = gson.fromJson(json, resultType);
                Log.w("GsonParser", "parse response = " + o.toString());
            int objectCount = 0;
            if (o instanceof Response) {
                Response response = (Response) o;
                Log.w("GsonParser", "parse Object class = " + response.getClass());
                objectCount = (response != null && response.users != null) ? response.users.size() : 0;
            }
            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);
            return new ParseResult(duration, objectCount, o);*/

            long startTime = System.nanoTime();
            Response response = gson.fromJson(json, Response.class);
            int objectCount = (response != null && response.users != null) ? response.users.size() : 0;

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);

            return new ParseResult(duration, objectCount, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return new ParseResult();
    }

}
