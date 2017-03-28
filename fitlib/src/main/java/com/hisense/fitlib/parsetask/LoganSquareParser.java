package com.hisense.fitlib.parsetask;

import android.content.Context;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.model.User;
import com.hisense.fitlib.utils.Constants;
import com.hisense.fitlib.utils.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoganSquareParser extends Parser<Void, ParseResult> {

    public LoganSquareParser(ParseListener parseListener, String jsonString) {
        super(parseListener, jsonString);

    }

    public LoganSquareParser(Context context, ParseListener parseListener, String assetName) {
        super(context, parseListener, assetName);
    }

    @Override
    protected ParseResult parseAsset(Context mContext, String mAssertName) {
        try {
            long startTime = System.nanoTime();
            Response response = LoganSquare.parse(FileUtil.inputStreamAssetsFile(mContext, mAssertName), Response.class);

            int objectCount = (response != null && response.users != null) ? response.users.size() : 0;

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);

            return new ParseResult(duration, objectCount, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ParseResult();
    }

    @Override
    protected ParseResult parse(String json) {
        try {
            long startTime = System.nanoTime();
            Response response = LoganSquare.parse(json, Response.class);

            int objectCount = (response != null && response.users != null) ? response.users.size() : 0;

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);
            return new ParseResult(duration, objectCount, response);

            /*long startTime = System.nanoTime();

            T response = LoganSquare.parse(json, new com.bluelinelabs.logansquare.ParameterizedType<T>() {
            });

            int objectCount = (response != null && response.users != null) ? response.users.size() : 0;

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);

            return new ParseResult(duration, objectCount, response);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return new ParseResult();
    }

    private void foreachUserId(List<User> users) {
        for (User user: users) {
            if (Constants.TARGET_USER_ID.equalsIgnoreCase(user.id)) {
                Log.e("foreach", "get target user = " + user.id);
            }
        }
    }

}
