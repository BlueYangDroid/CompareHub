package com.hisense.fitlib.parsetask;

import android.content.Context;


import com.alibaba.fastjson.JSON;
import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.utils.FileUtil;

import java.util.concurrent.TimeUnit;

public class FastJsonParser extends Parser<Void, ParseResult> {


    public FastJsonParser(ParseListener parseListener, String jsonString) {
        super(parseListener, jsonString);
    }

    public FastJsonParser(Context context, ParseListener parseListener, String assetName) {
        super(context, parseListener, assetName);
    }

    @Override
    protected ParseResult parseAsset(Context mContext, String mAssertName) {
        return parse(FileUtil.readAssetsFile(mContext, mAssertName));
    }

    @Override
    protected ParseResult parse(String json) {
        try {
            long startTime = System.nanoTime();
            Response response = JSON.parseObject(json, Response.class);

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
