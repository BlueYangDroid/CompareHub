package com.hisense.fitlib.parsetask;

import android.content.Context;

import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.utils.FileUtil;
import com.squareup.moshi.Moshi;

import java.util.concurrent.TimeUnit;

public class MoshiParser extends Parser<Void, ParseResult> {

    private final Moshi moshi;

    public MoshiParser(ParseListener parseListener, String jsonString, Moshi moshi) {
        super(parseListener, jsonString);
        this.moshi = moshi;
    }

    public MoshiParser(Context context, ParseListener parseListener, String assetName, Moshi moshi) {
        super(context, parseListener, assetName);
        this.moshi = moshi;
    }

    @Override
    protected ParseResult parseAsset(Context mContext, String mAssertName) {
        return parse(FileUtil.readAssetsFile(mContext, mAssertName));
    }

    @Override
    protected ParseResult parse(String json) {
        try {
            long startTime = System.nanoTime();
            Response response = moshi.adapter(Response.class).fromJson(json);

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
