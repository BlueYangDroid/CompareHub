package com.hisense.fitlib.parsetask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;


import java.util.concurrent.TimeUnit;

public abstract class Parser<V, T> extends AsyncTask<V, V, T> {

    private static final int SOURCE_FLAG_STRING = 0;
    private static final int SOURCE_FLAG_ASSET = 1;
    private static final int SOURCE_FLAG_STREAM = 2;

    private final ParseListener mParseListener;
    private final int mSourceFlag;
    private String mJsonString;
    private Context mContext;
    private String mAssertName;

    public interface ParseListener {
        void onComplete(Parser parser, Object parseResult);
    }


    protected Parser(ParseListener parseListener, String jsonString) {
        mParseListener = parseListener;
        mJsonString = jsonString;
        mSourceFlag = SOURCE_FLAG_STRING;
    }

    protected Parser(Context context, ParseListener parseListener, String assertName) {
        mParseListener = parseListener;
        mAssertName = assertName;
        mContext = context;
        mSourceFlag = SOURCE_FLAG_ASSET;
    }

    @Override
    protected T doInBackground(V... params) {
        System.gc();
        long startTime = System.nanoTime();
        T result = null;
        if (mSourceFlag == SOURCE_FLAG_STRING && !TextUtils.isEmpty(mJsonString)) {
            result = parse(mJsonString);
        }
        else if (mSourceFlag == SOURCE_FLAG_ASSET && mContext != null && !TextUtils.isEmpty(mAssertName)) {
            result = parseAsset(mContext, mAssertName);
        }

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);

        return result;
    }

    @Override
    protected void onPostExecute(T parseResult) {
        mParseListener.onComplete(this, parseResult);
    }

    protected abstract T parseAsset(Context mContext, String mAssertName);

    protected abstract T parse(String json);
}
