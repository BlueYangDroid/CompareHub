package com.hisense.fitlib.parsetask;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.utils.FileUtil;

import java.util.concurrent.TimeUnit;

public class JacksonDatabindParser extends Parser<Void, ParseResult> {

    private final ObjectMapper objectMapper;

    public JacksonDatabindParser(ParseListener parseListener, String jsonString, ObjectMapper objectMapper) {
        super(parseListener, jsonString);
        this.objectMapper = objectMapper;
    }

    public JacksonDatabindParser(Context context, ParseListener parseListener, String assetName, ObjectMapper objectMapper) {
        super(context, parseListener, assetName);
        this.objectMapper = objectMapper;
    }

    @Override
    protected ParseResult parseAsset(Context mContext, String mAssertName) {
        return parse(FileUtil.readAssetsFile(mContext, mAssertName));
    }

    @Override
    protected ParseResult parse(String json) {
        try {
            long startTime = System.nanoTime();
            Response response = objectMapper.readValue(json, Response.class);
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
