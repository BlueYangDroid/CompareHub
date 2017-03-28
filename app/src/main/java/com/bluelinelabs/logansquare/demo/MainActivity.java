package com.bluelinelabs.logansquare.demo;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.demo.widget.BarChart;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hisense.fitlib.model.Response;
import com.hisense.fitlib.parsetask.FastJsonParser;
import com.hisense.fitlib.parsetask.GsonParser;
import com.hisense.fitlib.parsetask.JacksonDatabindParser;
import com.hisense.fitlib.parsetask.LoganSquareParser;
import com.hisense.fitlib.parsetask.ParseResult;
import com.hisense.fitlib.parsetask.Parser;
import com.hisense.fitlib.serialtask.GsonSerializer;
import com.hisense.fitlib.serialtask.JacksonDatabindSerializer;
import com.hisense.fitlib.serialtask.LoganSquareSerializer;
import com.hisense.fitlib.serialtask.MoshiSerializer;
import com.hisense.fitlib.serialtask.SerializeResult;
import com.hisense.fitlib.serialtask.Serializer;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This is a test app we wrote in 10 minutes. Please do not write code like this, kiddos.
 */

@SuppressWarnings("ALL")
public class MainActivity extends ActionBarActivity {

    private static final int ITERATIONS = 20;
    private static final String TAG = MainActivity.class.getSimpleName();

    private BarChart mBarChart;
    private List<String> mJsonStringsToParse;
    private List<Response> mResponsesToSerialize;

    private final Parser.ParseListener mParseListener = new Parser.ParseListener() {
        @Override
        public void onComplete(Parser parser, Object parseResult) {
            if (parseResult instanceof ParseResult) {
                ParseResult result = (ParseResult)parseResult;
                addBarData(parser, result);
            }
        }
    };
    private final Serializer.SerializeListener mSerializeListener = new Serializer.SerializeListener() {
        @Override
        public void onComplete(Serializer serializer, SerializeResult serializeResult) {
            addBarData(serializer, serializeResult);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJsonStringsToParse = readJsonFromFile();
        mResponsesToSerialize = getResponsesToParse();

        mBarChart = (BarChart)findViewById(R.id.bar_chart);
        mBarChart.setColumnTitles(new String[] {"GSON", "Jackson", "LoganSquare", "FastJson"});

        findViewById(R.id.btn_parse_tests).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performParseTests();
            }
        });

        findViewById(R.id.btn_parse_asset).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performParseAssetFiles();
            }
        });

        findViewById(R.id.btn_serialize_tests).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performSerializeTests();
            }
        });
    }

    @SuppressWarnings("UnusedAssignment")
    private void performParseTests() {
        mBarChart.clear();
        mBarChart.setSections(new String[] {"Parse 150 items", "Parse 60 items", "Parse 20 items", "Parse 7 items", "Parse 2 items"});

        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        Moshi moshi = new Moshi.Builder().build();
        List<Parser> parsers = new ArrayList<>();
        for (String jsonString : mJsonStringsToParse) {
            for (int iteration = 0; iteration < ITERATIONS; iteration++) {
                parsers.add(new GsonParser<Response>(mParseListener, jsonString, gson));
                parsers.add(new JacksonDatabindParser(mParseListener, jsonString, objectMapper));
                parsers.add(new LoganSquareParser(mParseListener, jsonString));
//                parsers.add(new MoshiParser(mParseListener, jsonString, moshi));
                parsers.add(new FastJsonParser(mParseListener, jsonString));
            }
        }

        for (Parser parser : parsers) {
            parser.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }
    @SuppressWarnings("UnusedAssignment")
    private void performParseAssetFiles() {
        mBarChart.clear();
        mBarChart.setSections(new String[] {"Parse 150 items", "Parse 60 items", "Parse 20 items", "Parse 7 items", "Parse 2 items"});

        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        Moshi moshi = new Moshi.Builder().build();
        List<Parser> parsers = new ArrayList<>();
        String[] assertFiles = new String[]{"largesample150.json", "largesample.json", "mediumsample.json", "smallsample.json", "tinysample.json"};
//        String[] assertFiles = new String[]{"mediumsample.json", "smallsample.json", "tinysample.json"};
        for (String  assetName: assertFiles) {
            for (int iteration = 0; iteration < ITERATIONS; iteration++) {
                parsers.add(new GsonParser<Response>(this, mParseListener, assetName, gson, Response.class));
                parsers.add(new JacksonDatabindParser(this, mParseListener, assetName, objectMapper));
                parsers.add(new LoganSquareParser(this, mParseListener, assetName));
//                parsers.add(new MoshiParser(this, mParseListener, assetName, moshi));
                parsers.add(new FastJsonParser(this, mParseListener, assetName));
            }
        }

        for (Parser parser : parsers) {
            parser.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void performSerializeTests() {
        mBarChart.clear();
        mBarChart.setSections(new String[] {"Serialize 150 items", "Serialize 60 items", "Serialize 20 items", "Serialize 7 items", "Serialize 2 items"});

        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        Moshi moshi = new Moshi.Builder().build();
        List<Serializer> serializers = new ArrayList<>();
        for (Response response : mResponsesToSerialize) {
            for (int iteration = 0; iteration < ITERATIONS; iteration++) {
                serializers.add(new GsonSerializer(mSerializeListener, response, gson));
                serializers.add(new JacksonDatabindSerializer(mSerializeListener, response, objectMapper));
                serializers.add(new LoganSquareSerializer(mSerializeListener, response));
                serializers.add(new MoshiSerializer(mSerializeListener, response, moshi));
            }
        }

        for (Serializer serializer : serializers) {
            serializer.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void addBarData(Parser parser, ParseResult parseResult) {
        int section;
        int countParsed = parseResult.countParsed;
        switch (countParsed) {
            case 150:
            case 120:
                section = 0;
                break;
            case 60:
                section = 1;
                break;
            case 20:
                section = 2;
                break;
            case 7:
                section = 3;
                break;
            case 2:
                section = 4;
                break;
            default:
                section = -1;
                break;
        }

        float timing = parseResult.runDuration / 1000f;
        if (parser instanceof GsonParser) {
            Log.d(TAG, "countParsed = " + countParsed + ", parser = GsonParser, timing = " + timing);
            mBarChart.addTiming(section, 0, timing);
        } else if (parser instanceof JacksonDatabindParser) {
            Log.d(TAG, "countParsed = " + countParsed + ", parser = JacksonDatabindParser, timing = " + timing);
            mBarChart.addTiming(section, 1, timing);
        } else if (parser instanceof LoganSquareParser) {
            Log.e(TAG, "countParsed = " + countParsed + ", parser = LoganSquareParser, timing = " + timing);
            mBarChart.addTiming(section, 2, timing);
        }/* else if (parser instanceof MoshiParser) {
            Log.d(TAG, "countParsed = " + countParsed + ", parser = MoshiParser, timing = " + timing);
            mBarChart.addTiming(section, 3, timing);
        } */else if (parser instanceof FastJsonParser) {
            Log.d(TAG, "countParsed = " + countParsed + ", parser = FastJsonParser, timing = " + timing);
            mBarChart.addTiming(section, 3, timing);
        }
    }

    private void addBarData(Serializer serializer, SerializeResult serializeResult) {
        int section;
        int countParsed = serializeResult.objectsParsed;
        Log.e(TAG, "addBarData(): countParsed = " + countParsed);
        switch (countParsed) {
            case 150:
            case 120:
                section = 0;
                break;
            case 60:
                section = 1;
                break;
            case 20:
                section = 2;
                break;
            case 7:
                section = 3;
                break;
            case 2:
                section = 4;
                break;
            default:
                section = -1;
                break;
        }

        if (serializer instanceof GsonSerializer) {
            mBarChart.addTiming(section, 0, serializeResult.runDuration / 1000f);
        } else if (serializer instanceof JacksonDatabindSerializer) {
            mBarChart.addTiming(section, 1, serializeResult.runDuration / 1000f);
        } else if (serializer instanceof LoganSquareSerializer) {
            mBarChart.addTiming(section, 2, serializeResult.runDuration / 1000f);
        } else if (serializer instanceof MoshiSerializer) {
            mBarChart.addTiming(section, 3, serializeResult.runDuration / 1000f);
        }
    }

    private List<Response> getResponsesToParse() {
        List<Response> responses = new ArrayList<>();

        try {
            for (String jsonString : mJsonStringsToParse) {
                responses.add(LoganSquare.parse(jsonString, Response.class));
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("The serializable objects were not able to load properly. These tests won't work until you completely kill this demo app and restart it.")
                    .setPositiveButton("OK", null)
                    .show();
        }

        return responses;
    }

    private List<String> readJsonFromFile() {
        List<String> strings = new ArrayList<>();

        strings.add(readFile("largesample150.json"));
        strings.add(readFile("largesample.json"));
        strings.add(readFile("mediumsample.json"));
        strings.add(readFile("smallsample.json"));
        strings.add(readFile("tinysample.json"));

        return strings;
    }

    private String readFile(String filename) {
        long startTime = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        InputStream json = null;
        BufferedReader in = null;
        try {
            json = getAssets().open(filename);
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }

            in.close();
            json.close();
        } catch (IOException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(
                        "The JSON file was not able to load properly. These tests won't work until you completely kill this demo app and restart it.")
                    .setPositiveButton("OK", null)
                    .show();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (json != null) {
                    json.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String toString = sb.toString();

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        Log.d(TAG, "init readFile = " + filename + ", duration = " + duration);

        return toString;
    }
}
