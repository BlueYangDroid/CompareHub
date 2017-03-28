package com.hisense.fitlib.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yanglijun.ex on 2017/3/20.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static String readAssetsFile(Context context, String filename) {
        Log.d(TAG, "readAssetsFile() ----");
        StringBuilder sb = new StringBuilder();
        InputStream json = null;
        BufferedReader in = null;
        try {
            json = context.getAssets().open(filename);
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            try {
//                if (json != null) {
//                    json.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return sb.toString();
    }

    public static InputStream inputStreamAssetsFile(Context context, String assetName) {
        Log.e(TAG, "inputStreamAssetsFile() ----");
        InputStream json = null;
        try {
            json = context.getAssets().open(assetName);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (json != null) {
//                    json.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return null;
    }

}
